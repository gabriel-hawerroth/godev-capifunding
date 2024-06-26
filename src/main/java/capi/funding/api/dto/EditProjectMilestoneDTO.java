package capi.funding.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record EditProjectMilestoneDTO(
        @Size(max = 80)
        String title,

        String description,

        @Min(value = 1)
        @Max(value = 32767, message = "sequence out of valid range")
        Integer sequence,

        Boolean completed,

        @PositiveOrZero
        @Digits(integer = 15, fraction = 2)
        BigDecimal contribution_goal
) {
}
