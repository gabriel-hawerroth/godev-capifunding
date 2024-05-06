create table project_spend(
    id serial primary key not null,
    project_id int4 not null,
    project_milestone_id int4,
    description varchar(100) not null,
    value numeric(15,2) not null,
    date date not null default current_date
);