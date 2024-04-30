package capi.funding.api.dto;

import capi.funding.api.models.User;
import jakarta.validation.constraints.*;

public record CreateUserDTO(
        @NotNull
        @NotBlank
        @Email
        String email,

        @NotNull
        @NotBlank
        @Size(min = 8, message = "The password must be at least 8 characters long")
        @Pattern.List({
                @Pattern(regexp = ".*[a-z].*", message = "The password must contain at least 1 lowercase letter"),
                @Pattern(regexp = ".*[A-Z].*", message = "The password must contain at least 1 uppercase letter"),
                @Pattern(regexp = ".*\\d.*", message = "The password must contain at least 1 number"),
                @Pattern(regexp = ".*[!@#$%^&*()_+{}\\[\\]:;,.<>/?~\\\\].*", message = "The password must contain at least 1 special character")
        })
        String password,

        @NotNull
        @NotBlank
        @Size(max = 100)
        String name
) {
    public User toUser() {
        return new User(this);
    }
}
