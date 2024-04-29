package capi.funding.api.controllers;

import capi.funding.api.dto.AuthenticationDTO;
import capi.funding.api.dto.LoginResponseDTO;
import capi.funding.api.enums.UserRole;
import capi.funding.api.exceptions.AuthException;
import capi.funding.api.models.User;
import capi.funding.api.repository.UserRepository;
import capi.funding.api.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCrypt;

    private final TokenService tokenService;

    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO authDTO) {
        final var usernamePassword = new UsernamePasswordAuthenticationToken(authDTO.email(), authDTO.password());

        final Authentication auth;
        try {
            auth = authenticationManager.authenticate(usernamePassword);
        } catch (BadCredentialsException ex) {
            throw new AuthException("Bad credentials");
        } catch (DisabledException ex) {
            throw new AuthException("Inactive user");
        }

        final String token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(
                new LoginResponseDTO((User) auth.getPrincipal(), token)
        );
    }

    @PostMapping("/new-user")
    public ResponseEntity<User> createNewUser(@RequestBody @Valid User user) {
        user.setPassword(bCrypt.encode(user.getPassword()));
        user.setRole(UserRole.USER.toString());
        user.setActive(true);
        user.setCreation_date(LocalDateTime.now());

        return ResponseEntity.status(201).body(userRepository.save(user));
    }
}
