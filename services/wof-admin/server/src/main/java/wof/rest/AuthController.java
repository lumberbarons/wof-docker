package wof.rest;

import lombok.extern.slf4j.Slf4j;
import wof.exception.AuthenticationException;
import wof.repository.AuthorityRepository;
import wof.repository.UserRepository;
import wof.repository.model.UserEntity;
import wof.rest.model.AuthRequestDTO;
import wof.rest.model.AuthResponseDTO;
import wof.rest.model.PasswordRequestDTO;
import wof.rest.model.ResetDTO;
import wof.rest.model.mapper.ModelMapper;
import wof.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${jwt.header}")
    private String tokenHeader;

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final ModelMapper modelMapper;

    public AuthController(UserRepository userRepository, AuthenticationManager authenticationManager,
                           JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder,  ModelMapper modelMapper,
                           @Qualifier("jwtUserDetailsService") UserDetailsService userDetailsService) {
        this.userRepository  = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/token")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequestDTO authenticationRequest) throws AuthenticationException {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PutMapping("/password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordRequestDTO passwordRequest, Principal principal) {
        return userRepository.findByEmail(principal.getName()).map(user -> {
            user.setPassword(passwordEncoder.encode(passwordRequest.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAccount(Principal principal) throws AuthenticationException {
        Optional<UserEntity> user = userRepository.findByEmail(principal.getName());
        return user.map(u -> ResponseEntity.ok(modelMapper.mapAccount(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/account/reset")
    public ResponseEntity<?> resetAccount(@RequestBody ResetDTO resetDTO) throws AuthenticationException {
        return userRepository.findByResetKey(resetDTO.getResetKey()).map(user -> {
            user.setPassword(passwordEncoder.encode(resetDTO.getPassword()));
            user.setResetTimestamp(null);
            user.setResetKey(null);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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
