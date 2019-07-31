package acme.async.completablefuture;

import org.junit.Test;
import rx.Observable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class FromToRxJava {

  public static final String MSG = "Don't panic";

  public static <T> CompletableFuture<T> fromSingleObservable(Observable<T> observable) {
    final CompletableFuture<T> future = new CompletableFuture<>();
    observable
        .doOnError(future::completeExceptionally)
        .single()
        .forEach(future::complete);
    return future;
  }

  public static <T> CompletableFuture<List<T>> fromObservable(Observable<T> observable) {
    final CompletableFuture<List<T>> future = new CompletableFuture<>();
    observable
        .doOnError(future::completeExceptionally)
        .toList()
        .forEach(future::complete);
    return future;
  }

  public static <T> Observable<T> toObservable(CompletableFuture<T> future) {
    return Observable.create(subscriber ->
        future.whenComplete((result, error) -> {
          if (error != null) {
            subscriber.onError(error);
          } else {
            subscriber.onNext(result);
            subscriber.onCompleted();
          }
        }));
  }

  @Test
  public void shouldConvertCompletedFutureToCompletedObservable() throws Exception {
    //given
    CompletableFuture<String> future = CompletableFuture.completedFuture("Abc");

    //when
    Observable<String> observable = toObservable(future);

    //then
    assertThat(observable.toList().toBlocking().single()).containsExactly("Abc");
  }

  @Test
  public void shouldConvertFailedFutureIntoObservableWithFailure() throws Exception {
    //given
    CompletableFuture<String> future = failedFuture(new IllegalStateException(MSG));

    //when
    Observable<String> observable = toObservable(future);

    //then
    final List<String> result = observable
        .onErrorReturn(Throwable::getMessage)
        .toList()
        .toBlocking()
        .single();
    assertThat(result).containsExactly(MSG);
  }

  @Test
  public void shouldConvertObservableWithManyItemsToFutureOfList() throws Exception {
    //given
    Observable<Integer> observable = Observable.just(1, 2, 3);

    //when
    CompletableFuture<List<Integer>> future = fromObservable(observable);

    //then
    assertThat(future.get(1, SECONDS)).containsExactly(1, 2, 3);
  }

  @Test
  public void shouldConvertObservableWithSingleItemToFuture() throws Exception {
    //given
    Observable<Integer> observable = Observable.just(1);

    //when
    CompletableFuture<Integer> future = fromSingleObservable(observable);

    //then
    assertThat(future.get(1, SECONDS)).isEqualTo(1);
  }

  <T> CompletableFuture<T> failedFuture(Exception error) {
    CompletableFuture<T> future = new CompletableFuture<>();
    future.completeExceptionally(error);
    return future;
  }
}

