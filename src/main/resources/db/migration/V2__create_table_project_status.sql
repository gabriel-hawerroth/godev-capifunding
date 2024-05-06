create table project_status(
    id serial primary key not null,
    description varchar(30)
);

insert into project_status(description) values
    ('Em planejamento'),
    ('Aguardando financiamento'),
    ('Em andamento'),
    ('Pausado'),
    ('Em revisão'),
    ('Concluído'),
    ('Cancelado');