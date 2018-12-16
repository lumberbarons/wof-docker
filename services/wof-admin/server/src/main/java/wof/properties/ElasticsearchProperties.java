package wof.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("wof.elasticsearch")
public class ElasticsearchProperties {
    private String host;
    private Integer port;

    private Spelunker spelunker;

    @Data
    public static class Spelunker {
        private String index;
        private String alias;
    }
}
