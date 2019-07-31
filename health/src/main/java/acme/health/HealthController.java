package acme.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class HealthController {

  static Logger logger = LoggerFactory.getLogger("acme.health");

  private static final Random random = new Random(1233321);

  private static final AtomicInteger maxDelayInMilli = new AtomicInteger(1000);

  @GetMapping(value = "/health")
  public ResponseEntity<String> health(@RequestParam(value="delay", required = false) String delay) {
    if (!StringUtils.isEmpty(delay)) {
      maxDelayInMilli.set(Integer.parseInt(delay));
    }
    delay(maxDelayInMilli.get());
    return ResponseEntity.ok("UP");
  }

  private void delay(int maxDelay) {
    long d = random.nextInt(maxDelay);
    try {
      Thread.sleep(d);
    } catch (InterruptedException e) {
      logger.warn("Sleep interrupted: ", e);
    }
    logger.info("replying after {} millis", d);
  }
}

