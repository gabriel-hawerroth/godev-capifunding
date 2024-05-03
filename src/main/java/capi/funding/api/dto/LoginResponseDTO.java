package capi.funding.api.dto;

import capi.funding.api.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginResponseDTO(
        @NotNull
        User user,

        @NotNull
        @NotBlank
        String token
) {
}
