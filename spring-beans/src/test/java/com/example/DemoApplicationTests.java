package com.example;

import com.jayway.restassured.response.Headers;
import com.jayway.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class DemoApplicationTests {

	@Value("${local.server.port}")
	private int port;

    //private RestTemplate restTemplate = new RestTemplate();

	@Test
	public void contextLoads() {
        Response response = given().when().get(resource("say/hello-elk"));

        // Get all headers
        Headers allHeaders = response.getHeaders();

        // Get a single header value:
        String headerName = response.getHeader("headerName");

        // Get all cookies as simple name-value pairs
        Map<String, String> allCookies = response.getCookies();

        // Get a single cookie value:
        String cookieValue = response.getCookie("cookieName");

        // Get status line
        String statusLine = response.getStatusLine();

        // Get status code
        int statusCode = response.getStatusCode();
        assertThat(statusCode, is(200));
	}

	private String resource(String resource) {
		String url = String.format("http://localhost:%d/%s", this.port, resource);
        log.info(String.format("URL: %s", url));
		return url;
	}

}
