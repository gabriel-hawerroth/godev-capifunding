create table project_category(
    id serial primary key not null,
    name varchar(40)
);

insert into project_category(name) values
    ('Tecnologia'),
    ('Jogos'),
    ('Medicina'),
    ('Arte'),
    ('Moda'),
    ('Meio ambiente'),
    ('Saúde'),
    ('Esportes');