package acme.cb;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.RawJsonDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rx.Observable;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class UserController {

  private final static AtomicInteger counter = new AtomicInteger(1);

  @Autowired
  private AsyncBucket bucket;

  @Autowired
  private ObjectMapper objectMapper;

  @GetMapping(value = "/users/{id}")
  public ResponseEntity<User> get(@PathVariable(value="id") String id) {
    Optional<User> u = getDocumentAsync(id, User.class).toBlocking().firstOrDefault(Optional.empty());
    if (!u.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.ok(u.get());
  }

  @PostMapping(value = "/users")
  public ResponseEntity<Void> post(@RequestBody User user) {
    User u = User.of(String.valueOf(counter.getAndIncrement()), user.getName(), user.getAddress(), user.getInterests());
    RawJsonDocument rawJsonDocument = toRawJsonDocument(user.getId(), user);
    bucket.upsert(rawJsonDocument, 100L, TimeUnit.MILLISECONDS).toBlocking().first();

    bucket.touch(user.getId(), 10, 100L, TimeUnit.MILLISECONDS);
    return ResponseEntity.created(URI.create("/users/"+u.getId())).build();
  }

  private <T> RawJsonDocument toRawJsonDocument(String key, T objectToStore) {
    String jsonDocument = serialize(objectToStore, key);
    return RawJsonDocument.create(key, 10, jsonDocument);
  }

  private <T> Observable<Optional<T>> getDocumentAsync(String key, Class<T> type) {
    Observable<RawJsonDocument> rawJsonDocumentObservable = bucket.get(key, RawJsonDocument.class);
    return rawJsonDocumentObservable
            .timeout(100L, TimeUnit.MILLISECONDS)
            .switchIfEmpty(Observable.empty())
            .map(value -> Optional.ofNullable(value).map(v -> deserialize(v, type)));
  }

  private <T> String serialize(T document, String correlationId) {
    try {
      return objectMapper.writeValueAsString(document);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(String.format("Can't serialize %s", document.getClass().getSimpleName()), e);
    }
  }

  private <T> T deserialize(RawJsonDocument rawJsonDocument, Class<T> type) {
    try {
      return objectMapper.readValue(rawJsonDocument.content(), type);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Can't serialize %s", type.getSimpleName()), e);
    }
  }
}

