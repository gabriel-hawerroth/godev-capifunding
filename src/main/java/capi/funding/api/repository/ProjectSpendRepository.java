package capi.funding.api.repository;

import capi.funding.api.entity.ProjectSpend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectSpendRepository extends JpaRepository<ProjectSpend, Long> {

    @Query(value = """
            SELECT
                *
            FROM
                project_spend ps
            WHERE
                ps.project_id = :projectId
            ORDER BY
                ps.date desc
            """, nativeQuery = true)
    List<ProjectSpend> findByProject(long projectId);
}
