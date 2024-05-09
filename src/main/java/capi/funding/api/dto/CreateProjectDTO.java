package capi.funding.api.dto;

import capi.funding.api.entity.Project;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateProjectDTO(
        @NotNull
        @NotBlank
        @Size(max = 80)
        String title,

        @NotNull
        @NotBlank
        String description,

        @NotNull
        @Positive
        long category_id,

        @NotNull
        @Min(value = 1, message = "o id do status precisa ser válido")
        @Max(value = 7, message = "o id do status precisa ser válido")
        long status_id,

        Boolean need_to_follow_order,

        @FutureOrPresent
        LocalDate initial_date,

        @NotNull
        @Future
        LocalDate final_date
) {
    public Project toProject() {
        return new Project(this);
    }
}
