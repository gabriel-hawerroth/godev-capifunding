CREATE OR REPLACE FUNCTION public.count_total_searched_projects()
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
    total_count integer;
BEGIN
    SELECT
        COUNT(distinct p.id) INTO total_count
    FROM
        project_search_log psl
        JOIN project p ON
            (psl.filter_name = 'id' AND psl.filter_value::INTEGER = p.id
            OR psl.filter_name = 'project_title' AND p.title LIKE psl.filter_value)
        JOIN users u ON p.creator_id = u.id
        JOIN project_category pc ON p.category_id = pc.id
        JOIN project_status ps ON p.status_id = ps.id;

    RETURN total_count;
END;
$function$
;
