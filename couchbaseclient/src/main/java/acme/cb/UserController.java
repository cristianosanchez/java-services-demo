package acme.cb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class UserController {

  private final static AtomicInteger counter = new AtomicInteger(1);

  @Autowired
  private CouchbaseRepository repository;

  @GetMapping(value = "/users/{id}")
  public ResponseEntity<User> get(@PathVariable(value = "id") String id) {
    // repository.retrieveAsync(id);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @PostMapping(value = "/users")
  public ResponseEntity<Void> post(@RequestBody User user) {
    User u = User.of(String.valueOf(counter.getAndIncrement()), user.getName(), user.getAddress(), user.getInterests());
    repository.storeAsync(user.getId(), user);
    return ResponseEntity.created(URI.create("/users/" + u.getId())).build();
  }
}
