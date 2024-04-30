package capi.funding.api.controllers;

import capi.funding.api.dto.AuthenticationDTO;
import capi.funding.api.dto.CreateUserDTO;
import capi.funding.api.dto.LoginResponseDTO;
import capi.funding.api.models.User;
import capi.funding.api.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody AuthenticationDTO authDTO) {
        return ResponseEntity.ok(
                authService.doLogin(authDTO)
        );
    }

    @PostMapping("/new-user")
    public ResponseEntity<User> createNewUser(@RequestBody @Valid CreateUserDTO user) {
        return ResponseEntity.status(201).body(
                authService.createNewUser(user)
        );
    }
}
