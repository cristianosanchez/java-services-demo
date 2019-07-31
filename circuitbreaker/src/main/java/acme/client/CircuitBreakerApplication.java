package acme.client;

import com.netflix.hystrix.HystrixObservableCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.util.Random;

public class CircuitBreakerApplication {

    private static final Logger log = LoggerFactory.getLogger("acme.client");

	private static final Random random = new Random(1233321);

	public static void main(String[] args) {
		SimpleService hystrix = new HystrixServiceImpl();
		SimpleService resilience = new Resilience4jServiceImpl();
		(new CircuitBreakerApplication()).start(hystrix);
	}

	private void start(SimpleService service) {
		while(true) {
			service.call();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				log.warn("Sleep interrupted: ", e);
			}
		}
	}

	public void call() {

	}
}
