package acme.async.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.Callable;

@Slf4j
public class SimpleCallableTask implements Callable<String> {

  Random random = new Random();
  private String command;

  public SimpleCallableTask(String s) {
    this.command = s;
  }

  @Override
  public String call() throws Exception {
    processCommand();
    return command;
  }

  private void processCommand() {
    try {
      Thread.sleep(random.nextInt(3000));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}