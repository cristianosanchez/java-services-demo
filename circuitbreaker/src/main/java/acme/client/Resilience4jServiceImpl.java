package acme.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class Resilience4jServiceImpl implements SimpleService {

    private static final Logger log = LoggerFactory.getLogger("acme.client");
    private static final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void call() {

    }
}
