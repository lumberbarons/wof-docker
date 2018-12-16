package wof.repository.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name = "user")
public class UserEntity {

    @Id
    private String id;

    @Column(unique = true)
    private String email;

    private String name;

    private String password;

    private Long resetTimestamp;
    private String resetKey;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "authority"))
    public Set<AuthorityEntity> authorities;

}