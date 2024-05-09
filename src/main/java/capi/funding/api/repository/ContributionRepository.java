package capi.funding.api.repository;

import capi.funding.api.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    @Query(value = """
            SELECT
                *
            FROM
                contribution c
            WHERE
                c.project_id = :projectId
            ORDER BY
                c.date desc
            """, nativeQuery = true)
    List<Contribution> findByProject(long projectId);

    @Query(value = """
            SELECT
                COALESCE(SUM(c.value), 0)
            FROM
                contribution c
            """, nativeQuery = true)
    BigDecimal countTotalRaised();
}
