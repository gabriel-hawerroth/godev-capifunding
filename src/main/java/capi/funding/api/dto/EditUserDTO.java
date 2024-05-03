package capi.funding.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditUserDTO(
        @NotBlank
        @Size(max = 100)
        String name
) {
}
