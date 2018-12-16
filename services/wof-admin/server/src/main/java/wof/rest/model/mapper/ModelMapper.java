package wof.rest.model.mapper;

import wof.repository.model.AuthorityEntity;
import wof.repository.model.BundleEntity;
import wof.repository.model.UserEntity;
import wof.rest.model.AccountDTO;
import wof.rest.model.BundleDTO;
import wof.rest.model.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface ModelMapper {

    @Mapping(source = "authorities", target = "authorities", qualifiedByName = "mapAuthorities")
    AccountDTO mapAccount(UserEntity source);

    @Named("mapAuthorities")
    default List<String> mapAuthorities(Set<AuthorityEntity> authorities) {
        return authorities.stream().map(AuthorityEntity::getAuthority)
                .collect(Collectors.toList());
    }

    UserDTO map(UserEntity source);
    UserEntity map(UserDTO source);

    BundleDTO map(BundleEntity source);
    BundleEntity map(BundleDTO source);

}
