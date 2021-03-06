/*
 * Copyright 2018 Tigran Dadaiants
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.tddts.evetrader.view.fx.controller;


import com.github.tddts.evetrader.consts.RouteOption;
import com.github.tddts.evetrader.consts.SecurityLevel;
import com.github.tddts.evetrader.context.events.AuthorizationEvent;
import com.github.tddts.evetrader.context.events.UserDataEvent;
import com.github.tddts.evetrader.exception.SearchRunningException;
import com.github.tddts.evetrader.view.fx.dialog.SearchCancelConfirmationDialog;
import com.github.tddts.evetrader.view.fx.misc.choice.SecurityColorChangeListener;
import com.github.tddts.evetrader.view.fx.misc.table.SecurityColorTableCellFactory;
import com.github.tddts.evetrader.view.fx.misc.table.NumberFormatTableCellFactory;
import com.github.tddts.evetrader.view.fx.tools.message.MessageProvider;
import com.github.tddts.evetrader.model.app.OrderSearchRow;
import com.github.tddts.evetrader.model.app.SearchParams;
import com.github.tddts.evetrader.service.SearchService;
import com.github.tddts.evetrader.service.UserDataService;
import com.github.tddts.evetrader.service.UserInterfaceService;
import com.github.tddts.sprix.annotations.SprixController;
import com.github.tddts.sprix.beans.SprixDialogProvider;
import com.github.tddts.tools.fx.controls.DoubleTextField;
import com.github.tddts.tools.fx.controls.ItemListTextField;
import com.github.tddts.tools.fx.controls.PercentageTextField;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;


/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
@SprixController
public class SearchTabController {

  @FXML
  private TableColumn<OrderSearchRow, String> itemColumn;
  @FXML
  private TableColumn<OrderSearchRow, Long> quantityColumn;
  @FXML
  private TableColumn<OrderSearchRow, String> sellingColumn;
  @FXML
  private TableColumn<OrderSearchRow, String> buyingColumn;
  @FXML
  private TableColumn<OrderSearchRow, Double> volumeColumn;
  @FXML
  private TableColumn<OrderSearchRow, Double> volumeRemainColumn;
  @FXML
  private TableColumn<OrderSearchRow, Double> sellingPriceColumn;
  @FXML
  private TableColumn<OrderSearchRow, Double> buyingPriceColumn;
  @FXML
  private TableColumn<OrderSearchRow, Integer> jumpsColumn;
  @FXML
  private TableColumn<OrderSearchRow, Double> profitColumn;
  @FXML
  private TableColumn<OrderSearchRow, Double> perJumpColumn;

  @FXML
  private TableView<OrderSearchRow> searchTable;

  @FXML
  private DoubleTextField iskField;
  @FXML
  private DoubleTextField cargoField;
  @FXML
  private PercentageTextField taxField;
  @FXML
  private ItemListTextField<String> regionsField;

  @FXML
  private ChoiceBox<RouteOption> routeOptionBox;
  @FXML
  private ChoiceBox<SecurityLevel> minSecurityBox;
  @FXML
  private ChoiceBox<String> regionChoiceBox;

  @FXML
  private Button searchButton;
  @FXML
  private Button setFirstWaypointButton;
  @FXML
  private Button setLastWaypointButton;
  @FXML
  private Button openMarketButton;
  @FXML
  private Button regionSelectionButton;
  @FXML
  private Button clearRegionsButton;

  @Value("#{${static.regions}.keySet()}")
  private Set<String> regionNames;
  @Value("${view.pattern.isk}")
  private String iskPattern;

  @Autowired
  private MessageProvider messageProvider;
  @Autowired
  private SearchService searchService;
  @Autowired
  private UserInterfaceService userInterfaceService;
  @Autowired
  private UserDataService userDataService;
  @Autowired
  private SprixDialogProvider dialogProvider;

  @PostConstruct
  public void init() {
    initTable();
    initButtons();
    initChoiceBoxes();
  }

  private void initTable() {
    itemColumn.setCellValueFactory(cellData -> cellData.getValue().itemProperty());
    quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());

    sellingColumn.setCellValueFactory(cellData -> cellData.getValue().sellingLocationProperty());
    sellingColumn.setCellFactory(new SecurityColorTableCellFactory(false));

    buyingColumn.setCellValueFactory(cellData -> cellData.getValue().buyingLocationProperty());
    buyingColumn.setCellFactory(new SecurityColorTableCellFactory(true));

    volumeColumn.setCellValueFactory(cellData -> cellData.getValue().volumeProperty().asObject());
    volumeRemainColumn.setCellValueFactory(cellData -> cellData.getValue().volumeRemainProperty().asObject());

    sellingPriceColumn.setCellValueFactory(cellData -> cellData.getValue().sellPriceProperty().asObject());
    sellingPriceColumn.setCellFactory(new NumberFormatTableCellFactory(iskPattern));

    buyingPriceColumn.setCellValueFactory(cellData -> cellData.getValue().buyPriceProperty().asObject());
    buyingPriceColumn.setCellFactory(new NumberFormatTableCellFactory(iskPattern));

    jumpsColumn.setCellValueFactory(cellData -> cellData.getValue().jumpsProperty().asObject());

    profitColumn.setCellValueFactory(cellData -> cellData.getValue().profitProperty().asObject());
    profitColumn.setCellFactory(new NumberFormatTableCellFactory(iskPattern));

    perJumpColumn.setCellValueFactory(cellData -> cellData.getValue().perJumpProfitProperty().asObject());
    perJumpColumn.setCellFactory(new NumberFormatTableCellFactory(iskPattern));

    searchTable.setItems(FXCollections.observableArrayList());
  }

  private void initButtons() {
    searchButton.setOnAction(event -> searchAction());
    regionSelectionButton.setOnAction(event -> addRegion());
    clearRegionsButton.setOnAction(event -> clearRegions());

    setLastWaypointButton.setOnAction(event -> onSelectedRow(row -> userInterfaceService.setBuyWaypoint(row)));
    setFirstWaypointButton.setOnAction(event -> onSelectedRow(row -> userInterfaceService.setSellWaypoint(row)));
    openMarketButton.setOnAction(event -> onSelectedRow(row -> userInterfaceService.openMarketDetails(row)));
  }

  private void initChoiceBoxes() {
    routeOptionBox.setConverter(messageProvider.getConverter(RouteOption.values()));
    routeOptionBox.setItems(FXCollections.observableArrayList(RouteOption.values()));
    routeOptionBox.getSelectionModel().selectFirst();

    minSecurityBox.setItems(FXCollections.observableArrayList(SecurityLevel.values()));
    minSecurityBox.getSelectionModel().selectedItemProperty().addListener(new SecurityColorChangeListener(minSecurityBox));
    minSecurityBox.getSelectionModel().select(SecurityLevel.LEVEL_05);

    regionChoiceBox.setItems(FXCollections.observableArrayList(regionNames));
    regionChoiceBox.getSelectionModel().selectFirst();
  }

  //--------------------------------------------------------------------------------------------------------------------

  private void searchAction() {
    if (validateInput())
      doSearch();
  }

  private void doSearch() {
    try {
      searchService.searchForOrders(createSearchParams());
    } catch (SearchRunningException e) {
      processSearchRunningException();
    }
  }

  private void processSearchRunningException() {
    SearchCancelConfirmationDialog dialog = dialogProvider.getDialog(SearchCancelConfirmationDialog.class);
    Optional<ButtonType> result = dialog.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.OK)
      searchService.stopSearch();
  }

  private SearchParams createSearchParams() {
    return new SearchParams()
            .setIsk(iskField.getValue())
            .setCargo(cargoField.getValue())
            .setTax(taxField.getValue())
            .setRouteOption(routeOptionBox.getValue())
            .setSecurityLevel(minSecurityBox.getValue())
            .setRegions(getRegions())
            .setResultConsumer(this::fillSearchTable);
  }

  private void fillSearchTable(List<OrderSearchRow> resultList) {
    Platform.runLater(() -> {
      searchTable.getItems().clear();
      searchTable.getItems().addAll(resultList);
    });
  }

  private boolean validateInput() {
    //TODO: light up the input fields
    return !(iskField.getValue() == 0 || cargoField.getValue() == 0);

  }

  private void addRegion() {
    regionsField.add(regionChoiceBox.getSelectionModel().getSelectedItem());
  }

  private void clearRegions() {
    regionsField.removeAll();
  }

  private List<String> getRegions() {
    return regionsField.getList();
  }

  private void onSelectedRow(Consumer<OrderSearchRow> rowConsumer) {
    OrderSearchRow currentRow = searchTable.getSelectionModel().getSelectedItem();
    if (currentRow != null) rowConsumer.accept(currentRow);
  }

  @Subscribe
  private void processUserDataEvent(UserDataEvent event) {
    if (event.isCharacterIdSet()) setIskAmount();
  }

  @Subscribe
  private void processAuthorizationEvent(AuthorizationEvent event) {
    if (event.isAuthorized()) setIskAmount();
  }

  private void setIskAmount() {
    iskField.setText(userDataService.getWalletsAmountAsString());
  }
}
