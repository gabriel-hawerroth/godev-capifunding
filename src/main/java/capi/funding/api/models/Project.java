package capi.funding.api.models;

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

    @Column(nullable = false)
    private LocalDate initial_date;

    @Column(nullable = false)
    private LocalDate final_date;

    private byte[] cover_image;
}
