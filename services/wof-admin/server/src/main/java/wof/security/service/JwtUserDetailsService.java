package wof.security.service;

import wof.repository.UserRepository;
import wof.repository.model.AuthorityEntity;
import wof.security.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).map(user -> JwtUser.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getAuthorities().stream()
                        .map(AuthorityEntity::getAuthority).collect(Collectors.toList()))
                .build())
                .orElseThrow(() -> new UsernameNotFoundException(String
                        .format("No user found with username '%s'.", username)));
    }
}
