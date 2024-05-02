package capi.funding.api.repository;

import capi.funding.api.dto.InterfacesSQL;
import capi.funding.api.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(value = """
            SELECT
                p.id,
                p.title,
                (get_project_contribution_goal(p.id))
            FROM
                project p
                JOIN users u on p.creator_id = u.id
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
