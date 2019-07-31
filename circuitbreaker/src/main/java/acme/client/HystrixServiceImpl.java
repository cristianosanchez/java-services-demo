package acme.client;

import com.netflix.hystrix.HystrixObservableCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

public class HystrixServiceImpl implements SimpleService {

    private static final Logger log = LoggerFactory.getLogger("acme.client");

    @Override
    public void call() {
        HystrixObservableCommand command = new CheckHealthHystrixCommand();
        Observable<String> obs = command.toObservable();
        obs.subscribe(s -> log.info(s),
                e -> log.error("command error", e),
                () -> log.info("completed"));
        //return obs.subscribeOn(Schedulers.newThread()).toBlocking().firstOrDefault("");
    }
}
