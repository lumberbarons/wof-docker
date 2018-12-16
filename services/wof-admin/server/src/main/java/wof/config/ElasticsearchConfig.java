package wof.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wof.properties.ElasticsearchProperties;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient elasticsearchClient(ElasticsearchProperties elasticsearchProperties) {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticsearchProperties.getHost(),
                                elasticsearchProperties.getPort(), "http")));
    }

}
