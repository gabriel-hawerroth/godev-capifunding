create table users(
    id serial primary key not null,
    email varchar(100) unique not null,
    password varchar(60) not null,
    name varchar(255) not null,
    active bool not null default true,
    role varchar(5) not null default 'USER',
    profile_image bytea
);