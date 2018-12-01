package wof.repository.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "authority")
public class AuthorityEntity {
    @Id
    public String authority;
}
