package acme.async.completablefuture;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.*;

public class AsPromises {

	private static final Logger log = LoggerFactory.getLogger(AsPromises.class);

	private static final ScheduledExecutorService pool =
			Executors.newScheduledThreadPool(10,
					new ThreadFactoryBuilder()
							.setDaemon(true)
							.setNameFormat("FutureOps-%d")
							.build()
			);

	public static <T> CompletableFuture<T> never() {
		return new CompletableFuture<>();
	}

	public static <T> CompletableFuture<T> timeoutAfter(
			Duration duration) {
		final CompletableFuture<T> promise = new CompletableFuture<>();
		pool.schedule(
				() -> promise.completeExceptionally(new TimeoutException()),
				duration.toMillis(), TimeUnit.MILLISECONDS);
		return promise;
	}

}

