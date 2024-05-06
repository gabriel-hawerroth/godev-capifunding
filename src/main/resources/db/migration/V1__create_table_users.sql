create table users(
    id serial primary key not null,
    email varchar(100) unique not null,
    password varchar(60) not null,
    name varchar(255) not null,
    active bool not null default true,
    creation_date timestamp not null default current_timestamp,
    profile_image bytea
);
