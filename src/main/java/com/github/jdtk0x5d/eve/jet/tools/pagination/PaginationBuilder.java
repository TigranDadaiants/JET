package com.github.jdtk0x5d.eve.jet.tools.pagination;

import com.github.jdtk0x5d.eve.jet.rest.RestResponse;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Tigran_Dadaiants dtkcommon@gmail.com
 */
public class PaginationBuilder<E, T extends Collection<E>> {

  private Function<Integer, RestResponse<T>> loadFunction;
  private Consumer<T> loadingResultConsumer;
  private Consumer<PaginationErrorHandler> errorConsumer;

  private int firstPage = 1;
  private int lastPage = -1;

  private int retryNumber = 3;
  private long retryTimeout = 100;
  private boolean skipPageOnRetry = true;

  public PaginationBuilder() {
  }

  public PaginationBuilder<E, T> loadPage(Function<Integer, RestResponse<T>> loadFunction) {
    this.loadFunction = loadFunction;
    return this;
  }

  public PaginationBuilder<E, T> processPage(Consumer<T> loadingResultConsumer) {
    this.loadingResultConsumer = loadingResultConsumer;
    return this;
  }

  public PaginationBuilder<E, T> onError(Consumer<PaginationErrorHandler> errorConsumer){
    this.errorConsumer = errorConsumer;
    return this;
  }

  public PaginationBuilder<E, T> startWith(int page) {
    firstPage = page < 1 ? 1 : page;
    return this;
  }

  public PaginationBuilder<E, T> finishOn(int page) {
    lastPage = page;
    return this;
  }

  public PaginationBuilder<E, T> retryNumber(int retryNumber){
    this.retryNumber = retryNumber;
    return this;
  }

  public PaginationBuilder<E, T> retryTimeout(long retryTimeout){
    this.retryTimeout = retryTimeout;
    return this;
  }

  public PaginationBuilder<E, T> skiPageOnRetry(boolean skipPageOnRetry){
    this.skipPageOnRetry = skipPageOnRetry;
    return this;
  }

  public Pagination build() {
    if (loadFunction == null || loadingResultConsumer == null || errorConsumer == null) {
      throw new IllegalStateException("Pagination can not be initialized! Some of required fields are NULL!");
    }
    return new PaginationImpl<>(loadFunction, loadingResultConsumer, errorConsumer,
        firstPage, lastPage,
        retryNumber, retryTimeout, skipPageOnRetry);
  }
}
