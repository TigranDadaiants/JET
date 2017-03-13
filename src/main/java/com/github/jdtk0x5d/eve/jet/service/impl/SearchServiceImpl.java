package com.github.jdtk0x5d.eve.jet.service.impl;

import com.github.jdtk0x5d.eve.jet.config.spring.annotations.Profiling;
import com.github.jdtk0x5d.eve.jet.consts.DotlanRouteOption;
import com.github.jdtk0x5d.eve.jet.consts.OrderType;
import com.github.jdtk0x5d.eve.jet.dao.CacheDao;
import com.github.jdtk0x5d.eve.jet.model.api.dotlan.DotlanRoute;
import com.github.jdtk0x5d.eve.jet.model.api.esi.market.MarketOrder;
import com.github.jdtk0x5d.eve.jet.model.api.esi.universe.UniverseName;
import com.github.jdtk0x5d.eve.jet.model.app.OrderSearchRow;
import com.github.jdtk0x5d.eve.jet.model.app.SearchParams;
import com.github.jdtk0x5d.eve.jet.model.db.MarketPriceCache;
import com.github.jdtk0x5d.eve.jet.model.db.OrderSearchCache;
import com.github.jdtk0x5d.eve.jet.model.db.OrderSearchResult;
import com.github.jdtk0x5d.eve.jet.rest.RestResponse;
import com.github.jdtk0x5d.eve.jet.rest.api.dotlan.DotlanAPI;
import com.github.jdtk0x5d.eve.jet.rest.api.esi.MarketAPI;
import com.github.jdtk0x5d.eve.jet.rest.api.esi.UniverseAPI;
import com.github.jdtk0x5d.eve.jet.service.SearchService;
import com.github.jdtk0x5d.eve.jet.tools.pagination.Pagination;
import com.github.jdtk0x5d.eve.jet.tools.pagination.PaginationBuilder;
import com.github.jdtk0x5d.eve.jet.tools.pagination.PaginationErrorHandler;
import com.github.jdtk0x5d.eve.jet.tools.pagination.PaginationExecutor;
import com.github.jdtk0x5d.eve.jet.tools.tasks.TaskQueue;
import com.github.jdtk0x5d.eve.jet.util.RestUtil;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.jdtk0x5d.eve.jet.context.events.SearchStatusEvent.*;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
@Component
@Scope("singleton")
public class SearchServiceImpl implements SearchService {

  @Autowired
  private CacheDao cacheDao;
  @Autowired
  private MarketAPI marketAPI;
  @Autowired
  private DotlanAPI dotlanAPI;
  @Autowired
  private UniverseAPI universeAPI;

  @Autowired
  private EventBus eventBus;

  @Value("#{${static.regions}}")
  private Map<String, Integer> regionsMap;

  @Value("${service.search.expiration.timeout}")
  private int expirationTimeout;


  private PaginationExecutor paginationExecutor = new PaginationExecutor();
  private TaskQueue taskQueue = TaskQueue.emptyQueue();

  @Override
  @Profiling
  public void searchForOrders(SearchParams searchParams) {
    createTaskQueue(searchParams).execute();
  }

  /**
   * Creates new TaskQueue using given parameters.
   * Waits for last TaskQueue to finish execution.
   *
   * @param searchParams search parameters
   */
  private synchronized TaskQueue createTaskQueue(SearchParams searchParams) {

    taskQueue.stopAndWait();

    taskQueue = TaskQueue.create()
        // Load market prices
        .run(this::loadPrices)
        // Load market orders
        .run(() -> loadOrders(searchParams.getRegions()))
        // Stop loading orders when queue execution is stopped
        .onStop(() -> paginationExecutor.stop())
        // Filter loaded orders
        .run(() -> filter(searchParams.getIsk(), searchParams.getCargo()))
        // Find profitable orders
        .supply(() -> find(searchParams.getRouteOption(), searchParams.getCargo(), searchParams.getTax()))
        // Supply orders to result consumer
        .consume(searchParams.getResultConsumer())
        // Clean cached data
        .finallyAction(this::cleanUp);

    return taskQueue;
  }

  @Override
  public void stopSearch() {
    taskQueue.stop();
  }

  /**
   * Clear all cached data.
   */
  private void cleanUp() {
    eventBus.post(CLEARING_CACHE);
    cacheDao.deleteAll(OrderSearchCache.class);
    cacheDao.deleteAll(MarketPriceCache.class);
    eventBus.post(FINISHED);
  }

  /**
   * Loads prices for all items in the market.
   */
  private void loadPrices() {
    eventBus.post(LOADING_PRICES);
    marketAPI.getAllItemPrices().process(list -> cacheDao.saveAll(list.stream().map(MarketPriceCache::new).collect(Collectors.toList())));
  }

  /**
   * Loads orders for given set of regions.
   *
   * @param regions set of regions for which orders will be loaded.
   */
  private void loadOrders(Collection<String> regions) {
    eventBus.post(LOADING_ORDERS);

    // Get region IDs by names
    Collection<Integer> regionIds = regions == null || regions.isEmpty() ?
        // All regions
        regionsMap.values() : // or
        // Only required regions
        regions.stream().map(region -> regionsMap.get(region)).collect(Collectors.toList());

    // Run paginated loading of orders
    regionIds.forEach(region -> paginationExecutor.add(createPaginationForRegion(region)));
    paginationExecutor.execute();
  }

  /**
   * Creates Pagination object for given region id
   *
   * @param regionId region id
   * @return pagination object
   */
  private Pagination createPaginationForRegion(Integer regionId) {
    return new PaginationBuilder<MarketOrder, List<MarketOrder>>()
        // Load market orders for given region and page
        .loadPage(page -> marketAPI.getOrders(OrderType.ALL, regionId, page))
        // Convert and save loaded orders to DB
        .processPage(orders -> cacheDao.saveAll(orders.stream().map(OrderSearchCache::new).collect(Collectors.toList())))
        // Retry page loading on error
        .onError(PaginationErrorHandler::retryPage)
        // Build pagination
        .build();
  }

  /**
   * Filter loaded orders by amount of available funds and space.
   * Also deletes duplicate orders and orders that are soon to expire.
   *
   * @param funds  amount of funds available
   * @param volume amount of cargo volume available
   */
  private void filter(long funds, double volume) {
    eventBus.post(FILTERING_ORDERS);

    cacheDao.removeDuplicateOrders();
    cacheDao.removeSoonExpiredOrders(expirationTimeout);
    cacheDao.removeLargeItemOrders(volume);
    cacheDao.removeTooExpensiveOrders(funds);
  }

  /**
   * Returns list of profitable orders using given parameters
   *
   * @param routeOption route option
   * @param volume      available cargo volume
   * @param taxRate     tax rate
   * @return list of profitable orders
   */
  private List<OrderSearchRow> find(DotlanRouteOption routeOption, double volume, double taxRate) {
    eventBus.post(SEARCHING_FOR_PROFIT);

    List<OrderSearchResult> searchResults = cacheDao.findProfitableOrders(routeOption.getSecurity(), volume, taxRate);

    if (searchResults.isEmpty()) {
      eventBus.post(NO_ORDERS_FOUND);
      return Collections.emptyList();
    }

    // Loaded names for given types
    Map<Integer, String> typeNames = RestUtil.safeRequest(() -> universeAPI.getNames(
        // Get ids of types of loaded items
        searchResults.stream().mapToInt(OrderSearchResult::getTypeId).distinct().toArray()))
        // Convert loaded names to the map of type ids and names
        .getObject().stream().collect(Collectors.toMap(UniverseName::getId, UniverseName::getName));

    eventBus.post(SEARCHING_FOR_ROUTES);

    List<OrderSearchRow> result = searchResults.stream()
        .map(searchResult -> findRoute(searchResult, routeOption, typeNames.get(searchResult.getTypeId())))
        .collect(Collectors.toList());

    eventBus.post(FINISHED);
    return result;
  }

  /**
   * Finds route and creates OrderSearchRow using given parameters
   *
   * @param searchResult search result
   * @param routeOption  route option
   * @param typeName     item type name
   * @return new OrderSearchRow
   */
  private OrderSearchRow findRoute(OrderSearchResult searchResult, DotlanRouteOption routeOption, String typeName) {
    String sellSystemName = cacheDao.findStationSystemName(searchResult.getSellLocation());
    String buySystemName = cacheDao.findStationSystemName(searchResult.getBuyLocation());

    RestResponse<DotlanRoute> dotlanRouteResponse = RestUtil.safeRequest(
        () -> dotlanAPI.getRoute(routeOption, sellSystemName, buySystemName));

    return new OrderSearchRow(typeName, sellSystemName, buySystemName, searchResult, dotlanRouteResponse.getObject());
  }

}
