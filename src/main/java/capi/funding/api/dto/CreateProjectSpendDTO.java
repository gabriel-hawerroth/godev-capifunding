package capi.funding.api.dto;

import capi.funding.api.entity.ProjectSpend;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateProjectSpendDTO(
        @NotNull
        @Positive
        long project_id,

        @Positive
        Long project_milestone_id,

        @NotNull
        @NotBlank
        @Size(max = 100)
        String description,

        @NotNull
        @Positive
        @Digits(integer = 15, fraction = 2)
        BigDecimal value,

        @NotNull
        @PastOrPresent
        LocalDate date
) {
    public ProjectSpend toProjectSpend() {
        return new ProjectSpend(this);
    }
}
