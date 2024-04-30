package capi.funding.api.repository;

import capi.funding.api.models.ProjectMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectMilestoneRepository extends JpaRepository<ProjectMilestone, Long> {

    @Query(value = """
            SELECT
                *
            FROM
                project_milestone pm
            WHERE
                pm.project_id = :project_id
            ORDER BY
                pm.sequence
            """, nativeQuery = true)
    List<ProjectMilestone> findByProject(long project_id);

    @Query(value = """
            SELECT
            	pm.sequence + 1
            FROM
            	project_milestone pm
            WHERE
            	pm.project_id = :project_id
            ORDER BY
            	pm."sequence" DESC
            LIMIT 1;
            """, nativeQuery = true)
    Optional<Integer> findLastProjectSequence(long project_id);

    @Query(value = """
            SELECT
                *
            FROM
                project_milestone pm
            WHERE
                pm.project_id = :project_id
                AND pm.sequence = :sequence
            LIMIT 1
            """, nativeQuery = true)
    Optional<ProjectMilestone> findByProjectAndSequence(long project_id, int sequence);
}
