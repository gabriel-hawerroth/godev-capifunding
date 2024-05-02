package capi.funding.api.repository;

import capi.funding.api.models.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    List<Contribution> getProjectContributions(long projectId);
}
