package acme.async.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class SimpleRunnableTask implements Runnable {

  Random random = new Random();
  private String command;

  public SimpleRunnableTask(String s) {
    this.command = s;
  }

  @Override
  public void run() {
    log.info(Thread.currentThread().getName() + " Start. Command = " + command);
    processCommand();
    log.info(Thread.currentThread().getName() + " End.");
  }

  private void processCommand() {
    try {
      Thread.sleep(random.nextInt(3000));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}