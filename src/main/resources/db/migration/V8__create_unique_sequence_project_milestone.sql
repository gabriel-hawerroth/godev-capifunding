alter table project_milestone
add constraint project_sequence unique (project_id, sequence);