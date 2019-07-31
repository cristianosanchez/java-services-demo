package com.example.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MainConfiguration extends WebMvcConfigurerAdapter {

    // When beans conflict, consider marking one of the beans as @Primary,
    // updating the consumer to accept multiple beans, or using @Qualifier
    // to identify the bean that should be consumed.

    @Qualifier("simple")
    @Primary
    @Bean
    public String message() {
        return "hello, I'm a bean";
    }

    @Qualifier("complex")
    @Bean
    public String anotherMessage() {
        return "hello, I'm a another bean";
    }
}
