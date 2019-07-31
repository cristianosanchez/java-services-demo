package acme.async;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class HelloController {

  static Logger logger = LoggerFactory.getLogger("acme.async");

  private final ImplicitASyncService firstService;

  @GetMapping(value = "/hello-one/{msg}")
  public String helloOne(@PathVariable("msg") final String msg) {
    return firstService.sync(msg);
  }

  @GetMapping(value = "/hello-two/{msg}")
  public CompletableFuture<String> helloTwo(@PathVariable("msg") final String msg) {
    return firstService.async(msg);
  }
}
