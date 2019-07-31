package acme.async.completablefuture;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class HowToCompletableFuture {

    @Test
    public void exceptionally() {
        CompletableFuture<String> boom = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("this is not good");
        });
        boom.exceptionally(e -> "");
    }
}
