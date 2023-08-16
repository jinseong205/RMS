create table user_tb (
  user_id bigint auto_increment,
  password varchar(100) not null,
  roles varchar(255) default 'ROLE_USER',
  username varchar(100) not null,
  primary key (user_id)
);

alter table user_tb 
  add unique (username);

insert into user_tb(username, password, roles)
  values ('test1@test.com', '$2a$10$icDnUYdx2B7tf/vVyAyGde5JS0TBEfUVUFp7V84uf82lh0EgdYZca', 'ROLE_USER');
insert into user_tb(username, password, roles)
  values ('test2@test.com', '$2a$10$icDnUYdx2B7tf/vVyAyGde5JS0TBEfUVUFp7V84uf82lh0EgdYZca', 'ROLE_USER');
insert into user_tb(username, password, roles)
  values ('test3@test.com', '$2a$10$icDnUYdx2B7tf/vVyAyGde5JS0TBEfUVUFp7V84uf82lh0EgdYZca', 'ROLE_USER');


create table board_tb (
  board_id bigint auto_increment,
  crt_dt timestamp default current_timestamp,
  updt_dt timestamp default current_timestamp,
  crt_id varchar(255),
  updt_id varchar(255),
  content LONGTEXT,
  title varchar(100) not null,
  primary key (board_id)
);

insert into board_tb(title, content, crt_id, updt_id, crt_dt, updt_dt)
  values ('test1', 'test1', 'test1@test.com', 'test1@test.com', current_timestamp, current_timestamp);

insert into board_tb(title, content, crt_id, updt_id, crt_dt, updt_dt)
  values ('test2', 'test2', 'test1@test.com', 'test1@test.com', current_timestamp, current_timestamp);

insert into board_tb(title, content, crt_id, updt_id, crt_dt, updt_dt)
  values ('test3', 'test3', 'test1@test.com', 'test1@test.com', current_timestamp, current_timestamp);

insert into board_tb(title, content, crt_id, updt_id, crt_dt, updt_dt)
  values ('test4', 'test4', 'test1@test.com', 'test1@test.com', current_timestamp, current_timestamp);

insert into board_tb(title, content, crt_id, updt_id, crt_dt, updt_dt)
  values ('test5', 'test5', 'test1@test.com', 'test1@test.com', current_timestamp, current_timestamp);

  
insert into board_tb(title, content, crt_id, updt_id, crt_dt, updt_dt)
  values ('test6', 'test6', 'test1@test.com', 'test1@test.com', current_timestamp, current_timestamp);

insert into board_tb(title, content, crt_id, updt_id, crt_dt, updt_dt)
  values ('test7', 'test7', 'test1@test.com', 'test1@test.com', current_timestamp, current_timestamp);

insert into board_tb(title, content, crt_id, updt_id, crt_dt, updt_dt)
  values ('test8', 'test8', 'test1@test.com', 'test1@test.com', current_timestamp, current_timestamp);

insert into board_tb(title, content, crt_id, updt_id, crt_dt, updt_dt)
  values ('test9', 'test9', 'test1@test.com', 'test1@test.com', current_timestamp, current_timestamp);

insert into board_tb(title, content, crt_id, updt_id, crt_dt, updt_dt)
  values ('test10', 'test10', 'test1@test.com', 'test1@test.com', current_timestamp, current_timestamp);