package wof.initialization;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import wof.properties.ElasticsearchProperties;

import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@Component
public class ElasticsearchInitialization {

    private final ElasticsearchProperties elasticsearchProperties;
    private final RestHighLevelClient elasticsearchClient;

    private final ResourceLoader resourceLoader;

    public ElasticsearchInitialization(RestHighLevelClient elasticsearchClient,
                                       ElasticsearchProperties elasticsearchProperties,
                                       ResourceLoader resourceLoader) {
        this.elasticsearchClient = elasticsearchClient;
        this.elasticsearchProperties = elasticsearchProperties;
        this.resourceLoader = resourceLoader;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        GetIndexRequest existsRequest = new GetIndexRequest();
        existsRequest.indices(elasticsearchProperties.getSpelunker().getIndex());

        try {
            if(!elasticsearchClient.indices().exists(existsRequest, RequestOptions.DEFAULT)) {
                Resource mappings = resourceLoader.getResource("classpath:elasticsearch/mappings.spelunker.json");
                String json = new String(Files.readAllBytes(mappings.getFile().toPath()));

                CreateIndexRequest createRequest = new CreateIndexRequest(elasticsearchProperties.getSpelunker().getIndex());
                createRequest.source(json, XContentType.JSON).alias(new Alias(elasticsearchProperties.getSpelunker().getAlias()));
                CreateIndexResponse createIndexResponse = elasticsearchClient.indices()
                        .create(createRequest, RequestOptions.DEFAULT);

                log.info("Index created: {}", createIndexResponse);
            }
        } catch(IOException ex) {
            log.error("Failed to create spelunker index.", ex);
        }
    }

}
