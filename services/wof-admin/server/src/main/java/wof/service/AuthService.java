package wof.service;

import org.apache.commons.lang3.StringUtils;
import org.passay.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wof.util.IdGenerator;
import wof.exception.AuthenticationException;
import wof.exception.InvalidPasswordException;
import wof.repository.UserRepository;
import wof.repository.model.AuthorityEntity;
import wof.repository.model.UserEntity;
import wof.exception.AlreadyExistsException;
import wof.rest.model.*;
import wof.rest.model.mapper.ModelMapper;
import wof.security.JwtTokenUtil;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final ModelMapper modelMapper;

    private final PasswordValidator validator = new PasswordValidator(Arrays.asList(
            new LengthRule(8, 30),
            new UppercaseCharacterRule(1),
            new DigitCharacterRule(1),
            new SpecialCharacterRule(1),
            new NumericalSequenceRule(3,false),
            new AlphabeticalSequenceRule(3,false),
            new QwertySequenceRule(3,false),
            new WhitespaceRule()));

    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder, ModelMapper modelMapper,
                          @Qualifier("jwtUserDetailsService") UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponseDTO createToken(AuthRequestDTO authenticationRequest) {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return new AuthResponseDTO(token);
    }

    public void updatePassword(PasswordRequestDTO passwordRequest, Principal principal) {
        userRepository.findByEmail(principal.getName()).ifPresent(user -> {
            validatePassword(passwordRequest.getPassword());

            user.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
            userRepository.save(user);
        });
    }

    public Optional<AccountDTO> getAccount(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .map(modelMapper::mapAccount);
    }

    public Optional<AccountDTO> resetPassword(ResetDTO resetDTO) {
        return userRepository.findByResetKey(resetDTO.getResetKey()).map(user -> {
            validatePassword(resetDTO.getPassword());

            user.setPassword(passwordEncoder.encode(resetDTO.getPassword()));
            user.setResetTimestamp(null);
            user.setResetKey(null);

            UserEntity result = userRepository.save(user);

            return modelMapper.mapAccount(result);
        });
    }

    public EnvelopeDTO<UserDTO> createUser(UserDTO user) {
        UserEntity entity = modelMapper.map(user);

        entity.setId(IdGenerator.generate());
        entity.setAuthorities(new HashSet<>(Collections.singletonList(AuthorityEntity
                .builder().authority("ROLE_USER").build())));
        entity.setResetTimestamp(System.currentTimeMillis());
        entity.setResetKey(IdGenerator.reset());

        if (StringUtils.isBlank(entity.getEmail()) || userRepository.exists(Example.of(UserEntity
                .builder().email(entity.getEmail()).build()))) {
            throw new AlreadyExistsException("Invalid email or email exists!");
        }

        UserEntity result = userRepository.save(entity);
        EnvelopeDTO<UserDTO> envelope = new EnvelopeDTO<>(modelMapper.map(result));
        envelope.getMeta().put("resetKey", entity.getResetKey());

        return envelope;
    }

    public EnvelopeDTO<List<UserDTO>> getUsers(Pageable pageable) {
        Page<UserEntity> page = userRepository.findAll(pageable);
        List<UserDTO> list = page.getContent().stream()
                .map(modelMapper::map).collect(Collectors.toList());
        return new EnvelopeDTO<>(list, page);
    }

    private void validatePassword(String password) {
        RuleResult result = validator.validate(new PasswordData(password));
        if(!result.isValid()) {
            throw new InvalidPasswordException(result.toString());
        }
    }

    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthenticationException("User is disabled!", e);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Bad credentials!", e);
        }
    }

}
