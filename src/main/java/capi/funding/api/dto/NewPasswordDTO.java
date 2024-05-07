package capi.funding.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record NewPasswordDTO(
        @NotNull
        @NotBlank
        @Size(min = 8, message = "The password must be at least 8 characters long")
        @Pattern(regexp = ".*[a-z].*", message = "The password must contain at least 1 lowercase letter")
        @Pattern(regexp = ".*[A-Z].*", message = "The password must contain at least 1 uppercase letter")
        @Pattern(regexp = ".*\\d.*", message = "The password must contain at least 1 number")
        @Pattern(regexp = ".*[!@#$%^&*()_+{}\\[\\]:;,.<>/?~\\\\].*", message = "The password must contain at least 1 special character")
        String newPassword
) {
}
