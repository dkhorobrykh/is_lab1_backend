create table s367595.IS_user
(
    id bigserial primary key not null ,
    login varchar(40) not null,
    password text not null ,
    name text not null
);