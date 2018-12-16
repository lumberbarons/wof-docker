package wof.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import wof.exception.AuthenticationException;
import wof.rest.model.AuthRequestDTO;
import wof.rest.model.PasswordRequestDTO;
import wof.rest.model.ResetDTO;
import wof.service.AuthService;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService  = authService;
    }

    @PostMapping("/token")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequestDTO authenticationRequest) {
        return ResponseEntity.ok(authService.createToken(authenticationRequest));
    }

    @PutMapping("/password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordRequestDTO passwordRequest, Principal principal) {
        authService.updatePassword(passwordRequest, principal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/account")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAccount(Principal principal) {
        return authService.getAccount(principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/account/reset")
    public ResponseEntity<?> resetAccount(@RequestBody ResetDTO resetDTO) {
        return authService.resetPassword(resetDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
