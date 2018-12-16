package wof.repository.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "bundle")
public class BundleEntity {
    @Id
    private String id;

    private String name;

    private LocalDateTime lastProcessed;
    private LocalDateTime lastUpdated;
    private LocalDateTime lastModified;

    private String sha256;
}
