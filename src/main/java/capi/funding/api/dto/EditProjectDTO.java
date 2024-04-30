package capi.funding.api.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EditProjectDTO(
        @Size(max = 80)
        String title,

        String description,

        @Positive
        Long category_id,

        @Positive
        @Min(value = 1, message = "The status ID must be valid")
        @Max(value = 7, message = "The status ID must be valid")
        Long status_id,

        Boolean need_to_follow_order,

        @FutureOrPresent
        LocalDate final_date
) {
}
