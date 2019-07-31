package acme.async.completablefuture;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Examples adapted from amazing presentation of Tomasz Nurkiewicz (www.github.com/nurkiewicz/completablefuture)
 *
 */
@Slf4j
public class ASyncProgrammingCompletableFuture {

  public static final int TIME_TO_RANDOM = 2000;
  private Random random = new Random();

  /**
   * CompletableFuture basis
   *
   * get() Waits if necessary for this future to complete, and then returns its result.
   */
  @Test
  public void completed() throws Exception {
    CompletableFuture<Integer> answer =
        CompletableFuture.completedFuture(42);

    log.debug("Found: '{}'", answer.get());

//    CompletableFuture<Integer> answer2 = new CompletableFuture();
//    answer2.complete(42);
//
//    CompletableFuture<Integer> answer3 = new CompletableFuture();
//    answer3.completeExceptionally(new RuntimeException("Error!"));
  }

  /**
   * Built-in thread pool (ForkJoinPoll executor service)
   *
   * join() Returns the result value when complete, or throws an (unchecked) exception if completed exceptionally.
   */
  @Test
  public void supplyAsync() throws Exception {

    //Future<String> f = executor.submit(new SimpleCallableTask("" + i));

    CompletableFuture<String> answer =
        CompletableFuture.supplyAsync(() -> sleepThenReturnString());

    // answer.get()
    log.debug("Found: '{}'", answer.join());
  }

  /**
   * CompletableFuture<Void> thenAccept(Consumer<? super T> action)
   *
   * Returns void, so, end of line, no chain, although still async.
   */
  @Test
  public void supplyThenAccept() {
    CompletableFuture.supplyAsync(this::sleepThenReturnString)
        .thenAccept(System.out::println)
        .join();
  }

  /**
   * <U> CompletableFuture<U>	thenApply(Function<? super T,? extends U> fn)
   *
   * Returns CompletableFuture.
   * Note that the result U from Function will be wrapped into a new CompletableFuture<U>
   */
  @Test
  public void supplyThenApply() {
    CompletableFuture<String> stringResult =
        CompletableFuture.supplyAsync(this::sleepThenReturnString);

//    CompletableFuture<Integer> intResult =
//        stringResult.thenApply(Integer::parseInt);

    CompletableFuture<Integer> intResult =
        stringResult.thenApply(s -> Integer.parseInt(s));

    CompletableFuture<Integer> finalResult =
        intResult.thenApply(x -> 2 * x);

    CompletableFuture<Void> endOfLine =
        finalResult.thenAccept(System.out::println);

    endOfLine.join();
  }

  /**
   * <U> CompletableFuture<U>	thenApply(Function<? super T,? extends U> fn)
   *
   * Same example but now, chaining the results.
   */
  @Test
  public void supplyThenApplyChained() {
      CompletableFuture.supplyAsync(this::sleepThenReturnString)
          .thenApply(Integer::parseInt)
          .thenApply(x -> 2 * x)
          .thenAccept(System.out::println)
          .join();
  }

  /**
   * CompletableFuture<CompletableFuture<String>>...
   *
   * thenApply will double wrapping if you are calling a CompletableFuture result method.
   */
  @Test
  public void thenApplyWrongWay() throws Exception {
    CompletableFuture.supplyAsync(this::sleepThenReturnString)
        .thenApply(Integer::parseInt)

        // will double wrap
        .thenApply(this::sleepThenReturnASync)

        .thenAccept(System.out::println)
        .join();
  }

  /**
   * CompletableFuture<CompletableFuture<String>>...
   *
   * To avoid double wrapping, you might use callbacks, but this makes you uggly.
   */
  @Test
  public void thenApplyWrongWayEvenMore() throws Exception {
    CompletableFuture.supplyAsync(this::sleepThenReturnString)
        .thenApply(Integer::parseInt)

        // callback hell
        .thenAccept((Integer i) -> {
          sleepThenReturnASync(i)
              .thenAccept(System.out::println);
        })

        .join();
  }

  /**
   * <U> CompletableFuture<U>	thenCompose(Function<? super T,? extends CompletionStage<U>> fn)
   *
   * To avoid double wrapping, the right way, just use thenCompose.
   */
  @Test
  public void thenCompose() throws Exception {
    CompletableFuture.supplyAsync(this::sleepThenReturnString)
        .thenApply(Integer::parseInt)

        // unwrap the CompletableFuture
        .thenCompose( this::sleepThenReturnASync)

        .thenAccept(System.out::println)
        .join();
  }

  /**
   * <U,V> CompletableFuture<V>	thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn)
   *
   * Will wait for results along the chain.
   */
  @Test
  public void thenCombine() throws Exception {
    CompletableFuture<Integer> one = sleepThenReturnASync(2);
    CompletableFuture<Integer> two = sleepThenReturnASync(3);

    // chain both computations
    CompletableFuture<Integer> both =
        one.thenCombine(two, (Integer valueOne, Integer valueTwo) -> valueOne + valueTwo);

    both.thenAccept(length -> log.debug("Total length: {}", length));
  }

  /**
   * public <U> CompletableFuture<U> applyToEither(CompletionStage<? extends T> other, Function<? super T,U> fn)
   *
   * Will wait for the first result.
   */
  @Test
  public void either() throws Exception {
    CompletableFuture<Integer> one = sleepThenReturnASync(2);
    CompletableFuture<Integer> two = sleepThenReturnASync(3);

    CompletableFuture<Integer> both =
        one.applyToEither(two, value -> value);

    both.thenAccept(title -> log.debug("First: {}", title));
  }

  /**
   * public static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs)
   *
   * Will wait for all to complete, but returns void.
   */
  @Test
  public void allOf() throws Exception {
    CompletableFuture<String> one = CompletableFuture.supplyAsync(this::sleepThenReturnString);
    CompletableFuture<String> two = CompletableFuture.supplyAsync(this::sleepThenReturnString);
    CompletableFuture<Integer> three = sleepThenReturnASync(4);
    CompletableFuture<Integer> four = sleepThenReturnASync(5);

    // note, result is void. types don't need to be the same.
    CompletableFuture<Void> allCompleted =
        CompletableFuture.allOf(one, two, three, four);

    allCompleted.thenRun(() -> {
      try {
        log.debug("Loaded: {}", one.get());
        log.debug("Loaded: {}", two.get());
        log.debug("Loaded: {}", three.get());
        log.debug("Loaded: {}", four.get());
      } catch (InterruptedException | ExecutionException e) {
        log.error("", e);
      }
    });
  }

  /**
   * public static CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs)
   *
   * Will wait for first one to complete, but returns Object.
   */
  @Test
  public void anyOf() throws Exception {
    CompletableFuture<String> one = CompletableFuture.supplyAsync(this::sleepThenReturnString);
    CompletableFuture<String> two = CompletableFuture.supplyAsync(this::sleepThenReturnString);
    CompletableFuture<Integer> three = sleepThenReturnASync(4);
    CompletableFuture<Integer> four = sleepThenReturnASync(5);

    final CompletableFuture<Object> firstCompleted =
        CompletableFuture.anyOf(one, two, three, four);

    // note the Object as parameter!!
    firstCompleted.thenAccept((Object result) -> {
      log.debug("First: {}", result);
    });
  }

  /**
   * When exception happens along the chain, bellow steps will not execute
   */
  @Test
  public void exceptionsShortCircuitFuture() throws Exception {
    final CompletableFuture<String> answer
        = CompletableFuture.supplyAsync(this::sleepThenThrowException);

    // will not execute
    answer.thenApply(result -> {
      log.debug("Success!");
      return result;
    });

    // will actually throw the underlying exception wrapped into java.util.concurrent.ExecutionException
    answer.get();
  }

  /**
   * <U> CompletableFuture<U>	handle(BiFunction<? super T,Throwable,? extends U> fn)
   *
   * You might handle the result OR the exception.
   */
  @Test
  public void handleExceptions() throws Exception {
    //given
    CompletableFuture<String> answer
        = CompletableFuture.supplyAsync(this::sleepThenThrowException);

    //when
    CompletableFuture<String> recovered = answer
        .handle((result, throwable) -> {
          if (throwable != null) {
            return "No value returned: " + throwable;
          } else {
            return result.toUpperCase();
          }
        });

    //then
    log.debug("Handled: {}", recovered.get());
  }

  /**
   * public CompletableFuture<T> exceptionally(Function<Throwable,? extends T> fn)
   *
   * You might handle just the exception.
   */
  @Test
  public void shouldHandleExceptionally() throws Exception {
    //given
    CompletableFuture<String> answer
        = CompletableFuture.supplyAsync(this::sleepThenThrowException);

    //when
    CompletableFuture<String> recovered = answer
        .exceptionally(throwable -> "Sorry, try again later");

    //then
    log.debug("Done: {}", recovered.get());
  }

  private String sleepThenReturnString() {
    try {
      Thread.sleep(random.nextInt(TIME_TO_RANDOM));
    } catch (InterruptedException ignored) {
    }
    return "42";
  }

  private String sleepThenThrowException() {
    try {
      Thread.sleep(random.nextInt(TIME_TO_RANDOM));
    } catch (InterruptedException ignored) {
    }
    throw new RuntimeException("Some error");
  }

  private CompletableFuture<Integer> sleepThenReturnASync(Integer value) {
    try {
      Thread.sleep(random.nextInt(TIME_TO_RANDOM));
    } catch (InterruptedException ignored) {
    }
    return CompletableFuture.supplyAsync(() -> value * 2);
  }
}
