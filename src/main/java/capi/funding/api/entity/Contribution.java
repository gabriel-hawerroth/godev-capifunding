package capi.funding.api.entity;

import capi.funding.api.dto.CreateContributionDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contribution")
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive
    @Column(nullable = false)
    private long user_id;

    @Positive
    @Column(nullable = false)
    private long project_id;

    @Positive
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime date;

    public Contribution(CreateContributionDTO dto) {
        this.project_id = dto.project_id();
        this.value = dto.value();
    }
}
