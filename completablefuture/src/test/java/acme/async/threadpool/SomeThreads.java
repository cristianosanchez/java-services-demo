package acme.async.threadpool;

import lombok.SneakyThrows;
import org.junit.Test;

public class SomeThreads {

  @Test
  @SneakyThrows
  public void someThreads() {

    Thread t1 = new Thread(new SimpleRunnableTask("1" ));
    Thread t2 = new Thread(new SimpleRunnableTask("2" ));
    Thread t3 = new Thread(new SimpleRunnableTask("3" ));

    t1.start(); // will call Runnable::run() at some point
    t2.start();
    t3.start();

    t1.isAlive();

//    t1.join();
//    t2.join();
//    t3.join();
  }
}
