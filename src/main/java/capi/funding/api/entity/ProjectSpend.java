package capi.funding.api.entity;

import capi.funding.api.dto.CreateProjectSpendDTO;
import capi.funding.api.dto.EditProjectSpendDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_spend")
public class ProjectSpend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive
    @Column(nullable = false, updatable = false)
    private long project_id;

    private Long project_milestone_id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    @Column(nullable = false)
    private LocalDate date;

    public ProjectSpend(CreateProjectSpendDTO dto) {
        this.project_id = dto.project_id();
        this.project_milestone_id = dto.project_milestone_id();
        this.description = dto.description();
        this.value = dto.value();
        this.date = dto.date();
    }

    public void updateValues(EditProjectSpendDTO dto) {
        if (dto.project_milestone_id() != null) {
            this.project_milestone_id = dto.project_milestone_id();
        }

        if (dto.description() != null && !dto.description().isBlank()) {
            this.description = dto.description();
        }

        if (dto.value() != null) {
            this.value = dto.value();
        }

        if (dto.date() != null) {
            this.date = dto.date();
        }
    }
}
