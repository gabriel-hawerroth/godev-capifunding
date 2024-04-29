package capi.funding.api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
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
}
