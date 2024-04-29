create table project(
    id serial primary key not null,
    title varchar(80) not null,
    description text not null,
    creator_id int4 not null,
    category_id int4 not null,
    status_id int4 not null,
    need_to_follow_order bool not null default false,
    creation_date date not null default current_date,
    initial_date date not null,
    final_date date not null,
    cover_image bytea,
    foreign key (creator_id) references users(id),
    foreign key (category_id) references project_category(id),
    foreign key (status_id) references project_status(id)
);