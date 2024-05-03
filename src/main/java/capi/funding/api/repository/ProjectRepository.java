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
                p.coverImage,
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

//    just for demonstration, this is the code for function 'get_project_percentage_raised' from the 'getProjectsList()' method

//    CREATE OR REPLACE FUNCTION public.get_project_percentage_raised(project_id integer)
//          RETURNS numeric
//          LANGUAGE plpgsql
//    AS $function$
//    DECLARE
//          contribution_goal 	numeric;
//          total_contributions numeric;
//          percentage_raised	numeric(10,2);
//    BEGIN
//          select coalesce(sum(pm.contribution_goal), 0)
//          into contribution_goal
//          from project_milestone pm
//          where pm.project_id = get_project_percentage_raised.project_id;
//
//          select sum(c.value)
//          into total_contributions
//          from contribution c
//          where c.project_id = get_project_percentage_raised.project_id;
//
//	        if contribution_goal > 0 then
//              percentage_raised := coalesce((total_contributions / contribution_goal) * 100, 0);
//          elseif total_contributions > 0 then
//              percentage_raised := 100;
//	        else
//              percentage_raised := 0;
//          end if;
//
//	        return percentage_raised;
//    END;
//    $function$
//    ;
}
