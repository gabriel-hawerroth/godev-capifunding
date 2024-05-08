create table project_search_log(
    id bigserial primary key not null,
    user_id int4 not null,
    filter_name varchar(50) not null,
    filter_value text not null,
    search_date timestamp not null default current_timestamp,
    foreign key (user_id) references users(id)
);