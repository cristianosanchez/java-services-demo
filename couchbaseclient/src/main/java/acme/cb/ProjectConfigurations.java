package acme.cb;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class ProjectConfigurations {

    @Bean(destroyMethod = "close")
    public AsyncBucket createBucket() {
        Cluster cluster = couchbaseCluster();
        AsyncBucket bucket = cluster.openBucket("acme").async();
        return bucket;
    }

    /**
     * Cluster class implements the disconnect method, the one we're instructing Spring to callback during
     * shutdown. {@see Cluster#disconnect} for details.
     */
    @Bean(destroyMethod = "disconnect")
    public Cluster couchbaseCluster() {
        return CouchbaseCluster.create(couchbaseEnvironment(), Arrays.asList("localhost"))
                .authenticate("admin", "adminadmin");
    }

    /**
     * CouchbaseEnvironment class implements the shutdown method, the one we're instructing Spring to callback during
     * shutdown. {@see CoreEnvironment#shutdown} for details.
     */
    @Bean(destroyMethod = "shutdown")
    public CouchbaseEnvironment couchbaseEnvironment() {
        return DefaultCouchbaseEnvironment
                .builder()
                .socketConnectTimeout(5000)
                .bootstrapCarrierEnabled(false)
                .configPollInterval(60*1000)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
