CREATE OR REPLACE FUNCTION public.count_total_donated_projects()
    RETURNS integer
    LANGUAGE plpgsql
AS $function$
DECLARE
    total_count integer;
BEGIN
    SELECT
        COUNT(distinct p.id) INTO total_count
    FROM
        contribution c
        JOIN project p on c.project_id = p.id;

    RETURN total_count;
END;
$function$
;
