package capi.funding.api.repository;

import capi.funding.api.entity.ProjectMilestone;
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
                pm.project_id = :projectId
            ORDER BY
                pm.sequence asc
            """, nativeQuery = true)
    List<ProjectMilestone> findByProject(long projectId);

    @Query(value = """
            SELECT
            	pm.sequence + 1
            FROM
            	project_milestone pm
            WHERE
            	pm.project_id = :projectId
            ORDER BY
            	pm.sequence DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<Integer> findLastProjectSequence(long projectId);

    @Query(value = """
            SELECT
                *
            FROM
                project_milestone pm
            WHERE
                pm.project_id = :projectId
                AND pm.sequence = :sequence
                AND pm.id <> :id
            LIMIT 1
            """, nativeQuery = true)
    Optional<ProjectMilestone> findByProjectAndSequence(long projectId, int sequence, Long id);

    @Query(value = """
            SELECT
                *
            FROM
                project_milestone pm
            WHERE
                pm.project_id = :projectId
                AND pm.sequence < :sequence
                AND pm.completed is false
            """, nativeQuery = true)
    List<ProjectMilestone> findByProjectAndMinorSequence(long projectId, long sequence);

    @Query(value = """
            SELECT
                *
            FROM
                project_milestone pm
            WHERE
                pm.project_id = :projectId
                AND pm.completed is true
            ORDER BY
                pm.sequence ASC
            """, nativeQuery = true)
    List<ProjectMilestone> findByProjectAndCompleted(long projectId);
}
