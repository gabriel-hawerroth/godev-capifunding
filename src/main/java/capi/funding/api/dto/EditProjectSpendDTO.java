package capi.funding.api.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EditProjectSpendDTO(
        @Positive
        Long project_milestone_id,

        String description,

        @Positive
        @Digits(integer = 15, fraction = 2)
        BigDecimal value,

        @PastOrPresent
        LocalDate date
) {
}
