## Couchbase

(from https://blog.couchbase.com/spring-boot-couchbase-integration/)

Spring Boot now directly recognizes when you have the Couchbase SDK in your classpath. And when that's the case,
it instantiates a Cluster and a Bucket bean for you using autoconfiguration.

Spring Boot can pick up properties to further configure these core SDK classes, even the CouchbaseEnvironment!

See application.yml for more details.

## Logging

In the case of logging, the only mandatory dependency is Apache Commons Logging.
In Spring 5 (Spring Boot 2.x) it’s provided by Spring Framework’s spring-jcl module.
We shouldn’t worry about importing spring-jcl at all if we’re using a Spring Boot Starter
Then, set level value at application properties file:

  logging.level.root=INFO
  logging.level.trycb.web=DEBUG
  logging.level.org.springframework.web=INFO
  logging.level.org.springframework.bean=INFO
  logging.level.org.springframework.code=INFO
  logging.level.com.couchbase.client.java=DEBUG

When a file in the classpath has one of the following names, Spring Boot will automatically load it over the default configuration:

logback-spring.xml
logback.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <logger name="org.springframework.web" level="INFO"/>
    <logger name="com.couchbase" level="DEBUG"/>
    <logger name="com.goeuro.insurance" level="DEBUG"/>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%r [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <appender name="ANOTHER_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable
            </Pattern>
        </layout>
    </appender>
    <root level="INFO">
        <appender-ref ref="ANOTHER_CONSOLE"/>
    </root>
</configuration>
```