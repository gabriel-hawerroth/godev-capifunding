create table contribution(
    id serial primary key not null,
    user_id int4 not null,
    project_id int4 not null,
    value numeric(15,2) not null,
    date timestamp not null,
    foreign key (user_id) references users(id),
    foreign key (project_id) references project(id)
);