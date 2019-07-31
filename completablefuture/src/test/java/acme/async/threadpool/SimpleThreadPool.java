package acme.async.threadpool;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class SimpleThreadPool {

  @Test
  public void execute() {
    ExecutorService executor = Executors.newFixedThreadPool(5);

    for (int i = 0; i < 3; i++) {
      Runnable worker = new SimpleRunnableTask("" + i);
      executor.execute(worker);
    }

    // no new tasks will be accepted.
    executor.shutdown();

    // true when all tasks are finished
    while (!executor.isTerminated()) {
    }

    log.info("Finished all threads");
  }

  @SneakyThrows
  @Test
  public void submit() {
    ExecutorService executor = Executors.newFixedThreadPool(5);

    List<Future<String>> futures = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Future<String> f = executor.submit(new SimpleCallableTask("" + i));
      futures.add(f);
    }

    Thread.sleep(2000);

    futures.forEach(fs -> {
      if (fs.isCancelled()) {
        log.info("canceled");
      }

      if (fs.isDone()) {
        try {
          log.info("done: " + fs.get());
          //log.info("done: " + fs.get(1, TimeUnit.SECONDS));

          // TODO test timeout if Callable keeps alive

        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (ExecutionException e) {
          e.printStackTrace();
        }
      }
    });

    // while (!f.isDone()) {
    // log.info("waiting...");
    // }
  }
}
