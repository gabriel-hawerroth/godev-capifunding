package capi.funding.api.entity;

import capi.funding.api.dto.CreateProjectMilestoneDTO;
import capi.funding.api.dto.EditProjectMilestoneDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_milestone")
public class ProjectMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive
    @Column(nullable = false)
    private long project_id;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String title;

    @NotBlank
    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Min(value = 1)
    @Max(value = 32767)
    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    private boolean completed;

    @PositiveOrZero
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal contribution_goal;

    public ProjectMilestone(CreateProjectMilestoneDTO dto) {
        this.project_id = dto.project_id();
        this.title = dto.title();
        this.description = dto.description();

        if (dto.sequence() != null) {
            this.sequence = dto.sequence();
        }

        if (dto.completed() == null) {
            this.completed = false;
        } else {
            this.completed = dto.completed();
        }

        if (dto.contribution_goal() == null) {
            this.contribution_goal = BigDecimal.ZERO;
        } else {
            this.contribution_goal = dto.contribution_goal();
        }
    }

    public void updateValues(EditProjectMilestoneDTO dto) {
        if (dto.title() != null && !dto.title().isBlank()) {
            this.title = dto.title();
        }

        if (dto.description() != null && !dto.description().isBlank()) {
            this.description = dto.description();
        }

        if (dto.sequence() != null) {
            this.sequence = dto.sequence();
        }

        if (dto.completed() != null) {
            this.completed = dto.completed();
        }

        if (dto.contribution_goal() != null) {
            this.contribution_goal = dto.contribution_goal();
        }
    }
}
