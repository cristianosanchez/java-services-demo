package acme.client;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * HystrixCircuitBreaker is the interface that defines circuit-breaker logic that is hooked into HystrixCommand
 * execution and will stop allowing executions if failures have gone past the defined threshold.
 * It will then allow single retries after a defined sleepWindow until the execution succeeds at which point it will
 * again close the circuit and allow executions again.
 */
public class CheckHealthHystrixCommand extends HystrixObservableCommand<String> {

    private static final Logger log = LoggerFactory.getLogger("acme.client");
    private static final RestTemplate template = new RestTemplate();

    public CheckHealthHystrixCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("AcmeCircuitBreakerGroup"))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withExecutionTimeoutInMilliseconds(100)
                                // whether a circuit breaker will be used to track health and to short-circuit requests if it trips.
                                .withCircuitBreakerEnabled(true)
                                // the error percentage at or above which the circuit should trip open and start short-circuiting requests to fallback logic.
                                .withCircuitBreakerErrorThresholdPercentage(3)
                                // the minimum number of requests in a rolling window that will trip the circuit.
                                .withCircuitBreakerRequestVolumeThreshold(3)));
//        HystrixCircuitBreaker cb =
//                HystrixCircuitBreaker.Factory.getInstance(HystrixCommandKey.Factory.asKey("AcmeCircuitBreakerCommand"));
    }

    @Override
    protected Observable<String> construct() {
        return Observable.defer(() -> {
            Optional<String> response = callClient();
            if (response.isPresent()) {
                return Observable.just(response.get());
            }
            return Observable.empty();
        });
    }

    private Optional<String> callClient() throws RuntimeException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-GoEuro-Client", "CheckHealthCommand");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        URI uri = null;
        try {
            uri = new URI("http://localhost:8081/health");
            log.debug("Request URI {}", uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Malformed URI");
        }

        try {
            ResponseEntity<String> responseEntity =
                    template.exchange(uri, HttpMethod.GET, entity, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return Optional.of(responseEntity.getBody());
            }
            // unreachable code: spring http is garbage
            // and will throw RestClientException in this case
            // but it's unclear. Thanks Spring.
            log.warn("Service API not healthy: {}", responseEntity.getStatusCode());

        } catch (RestClientException e) {
            log.error("Unable to get response from simple-api: {}", e.getMessage());
            throw new RuntimeException("Error while calling service API");
        }
        return Optional.empty();
    }

    @Override
    protected Observable<String> resumeWithFallback() {
        log.error("Returning empty fallback");
        return Observable.empty();
    }
}
