package acme.cb;

import com.couchbase.client.java.PersistTo;
import com.couchbase.client.java.ReplicateTo;
import com.couchbase.client.java.query.consistency.ScanConsistency;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@Component
@EnableConfigurationProperties
@ConfigurationProperties("couchbase")
public class CouchbaseProperties {

    String hosts;
    String user;
    String password;
    String bucket;

    PersistTo persistTo;
    ReplicateTo replicateTo;

    ScanConsistency scanConsistency;

    int maxRetries;

    int writeTimeout;
    int connectTimeout;
    int disconnectTimeout;
    int managementTimeout;
    int queryTimeout;
    int viewTimeout;
    int searchTimeout;
    int kvTimeout;
    int analyticsTimeout;
    int keepAliveTimeout;
}
