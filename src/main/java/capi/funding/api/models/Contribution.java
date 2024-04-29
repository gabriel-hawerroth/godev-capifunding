package capi.funding.api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
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
}
