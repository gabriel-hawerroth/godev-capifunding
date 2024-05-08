package capi.funding.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_search_log")
public class ProjectSearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Long user_id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String filter_name;

    @NotBlank
    @Column(nullable = false, columnDefinition = "text")
    private String filter_value;

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime search_date;

    public ProjectSearchLog(Long user_id, String filter_name, String filter_value, LocalDateTime search_date) {
        this.user_id = user_id;
        this.filter_name = filter_name;
        this.filter_value = filter_value;
        this.search_date = search_date;
    }
}
