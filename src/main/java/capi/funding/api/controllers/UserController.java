package capi.funding.api.controllers;

import capi.funding.api.dto.UserEditDTO;
import capi.funding.api.models.User;
import capi.funding.api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/get-token-user")
    private ResponseEntity<User> getTokenUser() {
        final User user = userService.getTokenUser();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/change-password")
    private ResponseEntity<User> changePassword(@RequestParam long userId, @RequestParam String newPassword) {
        return userService.changePassword(userId, newPassword);
    }

    @PutMapping("/{id}")
    private ResponseEntity<User> editUser(@RequestBody @Valid UserEditDTO user) {
        return userService.editUser(user);
    }

    @PutMapping("/change-image")
    private ResponseEntity<User> changeProfileImage(@RequestParam MultipartFile file) {
        return userService.changeProfileImage(file);
    }
}
