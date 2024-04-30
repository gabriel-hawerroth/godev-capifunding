package capi.funding.api.models;

import capi.funding.api.dto.CreateProjectDTO;
import capi.funding.api.dto.EditProjectDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String title;

    @NotBlank
    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Positive
    @Column(nullable = false)
    private long creator_id;

    @Positive
    @Column(nullable = false)
    private long category_id;

    @Positive
    @Column(nullable = false)
    private long status_id;

    @Column(nullable = false)
    private boolean need_to_follow_order;

    @PastOrPresent
    @Column(nullable = false, updatable = false)
    private LocalDateTime creation_date;

    @Column(nullable = false, updatable = false)
    private LocalDate initial_date;

    @Column(nullable = false)
    private LocalDate final_date;

    private byte[] cover_image;

    public Project(CreateProjectDTO dto) {
        this.title = dto.title();
        this.description = dto.description();
        this.category_id = dto.category_id();
        this.status_id = dto.status_id();
        this.need_to_follow_order = dto.need_to_follow_order();
        this.final_date = dto.final_date();
        this.initial_date = dto.initial_date();
    }

    public void updateValues(EditProjectDTO dto) {
        if (dto.title() != null) {
            this.title = dto.title();
        }

        if (dto.description() != null) {
            this.description = dto.description();
        }

        if (dto.category_id() != null) {
            this.category_id = dto.category_id();
        }

        if (dto.status_id() != null) {
            this.status_id = dto.status_id();
        }

        if (dto.need_to_follow_order() != null) {
            this.need_to_follow_order = dto.need_to_follow_order();
        }

        if (dto.final_date() != null) {
            this.final_date = dto.final_date();
        }
    }
}
