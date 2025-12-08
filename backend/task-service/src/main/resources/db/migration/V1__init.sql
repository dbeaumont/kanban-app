create schema if not exists task_schema;

create table if not exists task_schema.tasks (
    id varchar(100) primary key,
    board_id varchar(100),
    column_id varchar(100),
    title varchar(255),
    description text,
    status varchar(50),
    assignee_id varchar(100),
    labels varchar(255),
    due_date timestamp,
    created_at timestamp,
    updated_at timestamp
);
