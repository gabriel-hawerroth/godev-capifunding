CREATE OR REPLACE FUNCTION public.get_project_percentage_raised(project_id integer)
    RETURNS numeric
    LANGUAGE plpgsql
AS $function$
DECLARE
    contribution_goal 	numeric;
    total_contributions numeric;
    percentage_raised	numeric(10,2);
BEGIN
     select coalesce(sum(pm.contribution_goal), 0)
     into contribution_goal
     from project_milestone pm
     where pm.project_id = get_project_percentage_raised.project_id;

     select sum(c.value)
     into total_contributions
     from contribution c
     where c.project_id = get_project_percentage_raised.project_id;

     if contribution_goal > 0 then
        percentage_raised := coalesce((total_contributions / contribution_goal) * 100, 0);
     elseif total_contributions > 0 then
        percentage_raised := 100;
	 else
        percentage_raised := 0;
     end if;

     return percentage_raised;
END;
$function$
;
