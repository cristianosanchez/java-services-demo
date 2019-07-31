package acme.cb;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Configuration
public class CouchbaseClientConfiguration {

  @Bean
  @Qualifier("thebucket")
  public AsyncBucket insuranceBucket(CouchbaseProperties properties) {
    return openBucket(properties, properties.getBucket());
  }

  @Bean(destroyMethod = "disconnect")
  public Cluster couchbaseCluster(CouchbaseProperties properties) {
    return CouchbaseCluster.create(couchbaseEnvironment(properties), Arrays.asList(properties.getHosts().split(",")))
        .authenticate(properties.getUser(), properties.getPassword());
  }

  @Bean(destroyMethod = "shutdown")
  public CouchbaseEnvironment couchbaseEnvironment(CouchbaseProperties properties) {
    return DefaultCouchbaseEnvironment.builder().socketConnectTimeout(properties.getConnectTimeout())
        .connectTimeout(properties.getConnectTimeout()).disconnectTimeout(properties.getDisconnectTimeout())
        .managementTimeout(properties.getManagementTimeout()).queryTimeout(properties.getQueryTimeout())
        .viewTimeout(properties.getViewTimeout()).searchTimeout(properties.getSearchTimeout())
        .kvTimeout(properties.getKvTimeout()).keepAliveTimeout(properties.getKeepAliveTimeout())
        .analyticsTimeout(properties.getAnalyticsTimeout()).build();
  }

  private AsyncBucket openBucket(CouchbaseProperties couchbaseProperties, String bucketName) {
    return couchbaseCluster(couchbaseProperties).openBucket(bucketName).async();
  }
}
