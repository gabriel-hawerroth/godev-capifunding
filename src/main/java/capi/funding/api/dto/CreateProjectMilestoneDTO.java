package capi.funding.api.dto;

import capi.funding.api.models.ProjectMilestone;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProjectMilestoneDTO(
        @NotNull
        @Positive
        long project_id,

        @NotNull
        @NotBlank
        @Size(max = 80)
        String title,

        @NotNull
        @NotBlank
        String description,

        @Min(value = 1)
        @Max(value = 32767, message = "sequence out of valid range")
        Integer sequence,

        Boolean completed,

        @PositiveOrZero
        @Digits(integer = 15, fraction = 2)
        BigDecimal contribution_goal
) {
    public ProjectMilestone toMilestone() {
        return new ProjectMilestone(this);
    }
}
