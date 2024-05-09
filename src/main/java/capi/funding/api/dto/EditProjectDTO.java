package capi.funding.api.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EditProjectDTO(
        @Size(max = 80)
        String title,

        String description,

        @Positive
        @Min(value = 1, message = "o id da categoria precisa ser válido")
        @Max(value = 8, message = "o id da categoria precisa ser válido")
        Long category_id,

        @Positive
        @Min(value = 1, message = "o id do status precisa ser válido")
        @Max(value = 7, message = "o id do status precisa ser válido")
        Long status_id,

        Boolean need_to_follow_order,

        @FutureOrPresent
        LocalDate final_date
) {
}
