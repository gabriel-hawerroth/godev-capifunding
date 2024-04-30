package capi.funding.api.controllers;

import capi.funding.api.dto.NewPasswordDTO;
import capi.funding.api.dto.UserEditDTO;
import capi.funding.api.models.User;
import capi.funding.api.services.UserService;
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
        return ResponseEntity.ok(
                userService.getTokenUser()
        );
    }

    @PutMapping("/change-password")
    private ResponseEntity<User> changePassword(@RequestParam NewPasswordDTO newPassword) {
        return ResponseEntity.ok(
                userService.changePassword(newPassword)
        );
    }

    @PutMapping("/{id}")
    private ResponseEntity<User> editUser(@RequestBody UserEditDTO userEditDTO) {
        return ResponseEntity.ok(
                userService.editUser(userEditDTO)
        );
    }

    @PutMapping("/change-image")
    private ResponseEntity<User> changeProfileImage(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(
                userService.changeProfileImage(file)
        );
    }
}
