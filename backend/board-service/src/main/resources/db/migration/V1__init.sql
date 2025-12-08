create schema if not exists board_schema;

create table if not exists board_schema.boards (
    id varchar(100) primary key,
    name varchar(255) not null,
    description text,
    owner_id varchar(100)
);
