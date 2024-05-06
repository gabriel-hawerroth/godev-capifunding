package capi.funding.api.repository;

import capi.funding.api.dto.InterfacesSQL;
import capi.funding.api.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(value = """
            SELECT
                p.id AS projectId,
                p.title AS projectTitle,
                p.cover_image AS coverImage,
                u.name AS creatorName,
                u.profile_image AS creatorProfileImage,
                p.final_date - current_date AS remainingDays,
                get_project_percentage_raised(p.id) AS percentageRaised
            FROM
                project p
                JOIN users u ON p.creator_id = u.id
            """, nativeQuery = true)
    List<InterfacesSQL.ProjectsList> getProjectsList();

    @Query(value = """
            SELECT
                *
            FROM
                project p
            WHERE
                p.final_date = :yesterday
                AND p.status_id <> 7
            """, nativeQuery = true)
    List<Project> findProjectsEndingYesterdayNotCancelled(LocalDate yesterday);
}
