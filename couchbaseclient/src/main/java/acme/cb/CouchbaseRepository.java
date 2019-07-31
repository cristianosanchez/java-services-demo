package acme.cb;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.document.RawJsonDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import rx.Observable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class CouchbaseRepository {

  private AsyncBucket bucket;
  private CouchbaseProperties couchbaseConfig;
  private ObjectMapper objectMapper;

  @Autowired
  public CouchbaseRepository(@Qualifier("thebucket") AsyncBucket bucket, CouchbaseProperties couchbaseConfig,
      ObjectMapper objectMapper) {
    this.bucket = bucket;
    this.couchbaseConfig = couchbaseConfig;
    this.objectMapper = objectMapper;
  }

  public <T> CompletableFuture<T> retrieveAsync(String key) {
    throw new IllegalAccessError("Not implemented yet");
  }

  public <T> CompletableFuture<Void> storeAsync(String key, T objectToStore) {
    final String documentId = key;
    return CompletableFuture.supplyAsync(() -> toRawJsonDocument(documentId, objectToStore))
        .thenCompose(rawJsonDocument -> toCompletableFuture(bucket
            .upsert(rawJsonDocument, couchbaseConfig.getPersistTo(), couchbaseConfig.getReplicateTo(),
                couchbaseConfig.getWriteTimeout(), TimeUnit.MILLISECONDS)
            .timeout(couchbaseConfig.getWriteTimeout(), TimeUnit.MILLISECONDS).switchIfEmpty(Observable.empty())))
        .thenAccept(a -> log.info("Persisted"));
  }

  private <T> RawJsonDocument toRawJsonDocument(String key, T objectToStore) {
    String jsonDocument = serialize(objectToStore);
    return RawJsonDocument.create(key, jsonDocument);
  }

  private <T> T deserialize(RawJsonDocument rawJsonDocument, Class<T> type) {
    try {
      return objectMapper.readValue(rawJsonDocument.content(), type);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Can't serialize %s", type.getSimpleName()), e);
    }
  }

  private <T> String serialize(T document) {
    try {
      return objectMapper.writeValueAsString(document);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(String.format("Can't serialize %s", document.getClass().getSimpleName()), e);
    }
  }

  private static <T> CompletableFuture<T> toCompletableFuture(Observable<T> observable) {
    final CompletableFuture<T> future = new CompletableFuture<>();
    observable.subscribe(future::complete, future::completeExceptionally);
    return future;
  }
}
