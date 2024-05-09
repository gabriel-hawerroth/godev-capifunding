CREATE OR REPLACE FUNCTION public.get_top_donated_projects(page_number integer DEFAULT 1)
    RETURNS TABLE(
        projectId integer,
        projectTitle character varying,
        coverImage bytea,
        creatorName character varying,
        creatorProfileImage bytea,
        remainingDays integer,
        percentageRaised numeric,
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
		contribution c
		JOIN project p ON c.project_id = p.id
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
	    COALESCE(sum(c.value),0) DESC
	OFFSET ((page_number - 1) * 10)
	LIMIT 10;
END;
$function$
;
