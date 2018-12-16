package wof.rest.model;

import lombok.Data;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
public class BundleDTO {
    private String id;
    private String name;
    private LocalDateTime lastProcessed;
    private LocalDateTime lastUpdated;
    private LocalDateTime lastModified;
    private String sha256;
}
