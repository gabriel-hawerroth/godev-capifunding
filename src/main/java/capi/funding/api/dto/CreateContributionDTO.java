package capi.funding.api.dto;

import capi.funding.api.entity.Contribution;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateContributionDTO(
        @NotNull
        @Positive
        long project_id,

        @Positive
        @Digits(integer = 15, fraction = 2)
        BigDecimal value
) {
    public Contribution toContribution() {
        return new Contribution(this);
    }
}
