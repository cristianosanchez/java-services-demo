package acme.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImplicitASyncService {

  private final Random random = new Random();

  public String sync(final String msg) {
    String s1 = wrap(msg, "[@]");
    String s2 = wrap(msg, "(@)");
    return s2;
  }

  public CompletableFuture<String> async(String msg) {
    return CompletableFuture.supplyAsync(() -> msg)
            .thenApply(m -> wrap(msg, "[@]"))
            .thenApply(m -> wrap(msg, "(@)"));
  }

  private String wrap(String msg, String wrap) {
    sleepRandom();
    return wrap.replace("@", msg);
  }

  private void sleepRandom() {
    try {
      Thread.sleep(random.nextInt(4000));
    } catch (InterruptedException e) {
      log.warn("Error while sleeping thread", e);
    }
  }
}
