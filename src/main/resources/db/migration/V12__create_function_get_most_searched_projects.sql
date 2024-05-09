CREATE OR REPLACE FUNCTION public.get_most_searched_projects(page_number integer DEFAULT 1)
 RETURNS TABLE(
 	projectid integer,
 	projecttitle character varying,
 	coverimage bytea,
 	creatorname character varying,
 	creatorprofileimage bytea,
 	remainingdays integer,
 	percentageraised numeric,
 	category character varying,
 	status character varying
 )
 LANGUAGE plpgsql
AS $function$
BEGIN
    RETURN QUERY
    SELECT
        p.id AS projectId,
        p.title AS projectTitle,
        p.cover_image AS coverImage,
        u.name AS creatorName,
        u.profile_image AS creatorProfileImage,
        GREATEST(p.final_date - CURRENT_DATE, 0) AS remainingDays,
        get_project_percentage_raised(p.id) AS percentageRaised,
        pc.name AS category,
        ps.description AS status
    FROM
        project_search_log psl
        JOIN project p ON
            (psl.filter_name = 'id' AND psl.filter_value::INTEGER = p.id
            OR psl.filter_name = 'project_title' AND p.title LIKE psl.filter_value)
        JOIN users u ON p.creator_id = u.id
        JOIN project_category pc ON p.category_id = pc.id
        JOIN project_status ps ON p.status_id = ps.id
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
        COUNT(psl.id) DESC
    OFFSET ((page_number - 1) * 10)
    LIMIT 10;
END;
$function$
;
