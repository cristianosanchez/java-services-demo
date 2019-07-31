package acme.cb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class CBClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(CBClientApplication.class, args);
	}
}
