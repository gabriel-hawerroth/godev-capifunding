package capi.funding.api.repository;

import capi.funding.api.dto.MostSearchedCategoriesDTO;
import capi.funding.api.dto.ProjectsList;
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
                GREATEST(p.final_date - current_date, 0) AS remainingDays,
                get_project_percentage_raised(p.id) AS percentageRaised,
                pc.name AS category,
                ps.description AS status
            FROM
                project p
                JOIN users u ON p.creator_id = u.id
                JOIN project_category pc ON p.category_id = pc.id
                JOIN project_status ps ON p.status_id = ps.id
                LEFT JOIN contribution c ON p.id = c.project_id
            WHERE
                ( :projectTitle = '' or LOWER(p.title) LIKE :projectTitle )
                AND ( (:projectCategory IS NULL) OR (p.category_id IN (:projectCategory)) )
                AND ( (:projectStatus IS NULL AND p.status_id not in (6,7)) OR (p.status_id IN (:projectStatus)) )
                AND ( :creatorName = '' OR LOWER(u.name) LIKE :creatorName )
            GROUP BY
            	projectId,
                projectTitle,
                coverImage,
                creatorName,
                creatorProfileImage,
                remainingDays,
                percentageRaised,
                category,
                status
            ORDER BY
            	COALESCE(SUM(c.value), 0) DESC
            OFFSET :offset
            limit :limit
            """, nativeQuery = true)
    List<ProjectsList> getProjectsList(
            String projectTitle, List<Integer> projectCategory, List<Integer> projectStatus, String creatorName,
            long offset, long limit
    );

    @Query(value = """
            SELECT
                COUNT(*)
            FROM
                project p
                JOIN users u ON p.creator_id = u.id
            WHERE
                ( :projectTitle = '' or LOWER(p.title) LIKE :projectTitle )
                AND ( (:projectCategory IS NULL) OR (p.category_id IN (:projectCategory)) )
                AND ( (:projectStatus IS NULL AND p.status_id not in (6,7)) OR (p.status_id IN (:projectStatus)) )
                AND ( :creatorName = '' OR LOWER(u.name) LIKE :creatorName )
            """, nativeQuery = true)
    long getTotalRegistersProjectsList(
            String projectTitle, List<Integer> projectCategory, List<Integer> projectStatus, String creatorName
    );

    @Query(value = """
            SELECT
                *
            FROM
                project p
            WHERE
                p.final_date = :yesterday
                AND p.status_id NOT IN (6,7)
            """, nativeQuery = true)
    List<Project> findProjectsEndingYesterdayNotCancelled(LocalDate yesterday);

    @Query(value = """
            SELECT *
            FROM get_most_searched_projects(:pageNumber)
            """, nativeQuery = true)
    List<ProjectsList> getMostSearchedProjects(int pageNumber);

    @Query(value = """
            SELECT *
            FROM count_total_searched_projects()
            """, nativeQuery = true)
    long countTotalSearchedProjects();

    @Query(value = """
            SELECT *
            FROM get_top_donated_projects(:pageNumber);
            """, nativeQuery = true)
    List<ProjectsList> getTopDonatedProjects(int pageNumber);

    @Query(value = """
            SELECT *
            FROM count_total_donated_projects();
            """, nativeQuery = true)
    long countTotalDonatedProjects();

    @Query(value = """
            SELECT
            	pc.name AS categoryName,
            	COUNT(pc.id) AS totalSearchs
            FROM
            	project_search_log psl
            	JOIN project_category pc
            	    ON (psl.filter_name = 'project_category' AND CAST(psl.filter_value AS integer) = pc.id)
            GROUP BY
            	categoryName
            ORDER BY
            	totalSearchs DESC
            """, nativeQuery = true)
    List<MostSearchedCategoriesDTO> getMostSearchedCategories();
}
