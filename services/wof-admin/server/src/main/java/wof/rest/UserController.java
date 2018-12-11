package wof.rest;

import lombok.extern.slf4j.Slf4j;
import wof.component.IdGenerator;
import wof.repository.UserRepository;
import wof.repository.model.AuthorityEntity;
import wof.repository.model.UserEntity;
import wof.rest.model.EnvelopePageDTO;
import wof.rest.model.EnvelopeDTO;
import wof.rest.model.UserDTO;
import wof.rest.model.mapper.ModelMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public UserController(ModelMapper modelMapper, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<EnvelopePageDTO<UserDTO>> getUsers(Pageable pageable) {
        Page<UserEntity> page = userRepository.findAll(pageable);
        List<UserDTO> list = page.getContent().stream()
                .map(modelMapper::map).collect(Collectors.toList());
        EnvelopePageDTO<UserDTO> payload = new EnvelopePageDTO<>(list, page);
        return ResponseEntity.ok(payload);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody UserDTO user) {
        UserEntity entity = modelMapper.map(user);

        entity.setId(IdGenerator.generate());
        entity.setAuthorities(new HashSet<>(Collections.singletonList(AuthorityEntity
                .builder().authority("ROLE_USER").build())));
        entity.setResetTimestamp(System.currentTimeMillis());
        entity.setResetKey(IdGenerator.reset());

        if (StringUtils.isBlank(entity.getName()) || userRepository.exists(Example.of(UserEntity
                .builder().name(entity.getName()).build()))) {
            return ResponseEntity.badRequest().body("Invalid name or name exists!");
        }

        if (StringUtils.isBlank(entity.getEmail()) || userRepository.exists(Example.of(UserEntity
                .builder().email(entity.getEmail()).build()))) {
            return ResponseEntity.badRequest().body("Invalid email or email exists!");
        }

        UserEntity result = userRepository.save(entity);
        EnvelopeDTO<UserDTO> payload = new EnvelopeDTO<>(modelMapper.map(result));
        payload.getMeta().put("resetKey", entity.getResetKey());
        return ResponseEntity.ok(payload);
    }
}
