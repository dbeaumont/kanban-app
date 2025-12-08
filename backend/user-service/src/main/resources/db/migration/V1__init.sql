create schema if not exists user_schema;

create table if not exists user_schema.users (
    id varchar(100) primary key,
    keycloak_user_id varchar(100) unique,
    display_name varchar(255),
    email varchar(255),
    preferences text
);
