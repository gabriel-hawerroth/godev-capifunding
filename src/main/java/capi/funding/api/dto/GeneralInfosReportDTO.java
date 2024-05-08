package capi.funding.api.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record GeneralInfosReportDTO(
        @PositiveOrZero
        long totalProjects,

        @NotNull
        @PositiveOrZero
        @Digits(integer = 20, fraction = 2)
        BigDecimal totalRaised,

        @PositiveOrZero
        long totalContributions
) {
}
