create table project_milestone(
    id serial primary key not null,
    project_id int4 not null,
    title varchar(80) not null,
    description text not null,
    sequence int2 not null,
    completed bool not null default false,
    contribution_goal numeric(15,2) not null,
    foreign key (project_id) references project(id)
);