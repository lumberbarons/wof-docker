package wof.component;

import lombok.extern.slf4j.Slf4j;
import wof.repository.AuthorityRepository;
import wof.repository.UserRepository;
import wof.repository.model.AuthorityEntity;
import wof.repository.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Slf4j
@Component
@Profile("development")
public class DatabaseInitialization {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!userRepository.existsById("admin")) {
            log.info("Adding default admin user...");
            authorityRepository.save(new AuthorityEntity("ROLE_USER"));
            authorityRepository.save(new AuthorityEntity("ROLE_ADMIN"));

            UserEntity user = UserEntity.builder().id(IdGenerator.generate()).email("admin")
                    .name("Administrator").password(passwordEncoder.encode("admin"))
                    .authorities(new HashSet<>(Arrays.asList(new AuthorityEntity("ROLE_USER"),
                            new AuthorityEntity("ROLE_ADMIN")))).build();

            userRepository.save(user);
        }
    }
}
