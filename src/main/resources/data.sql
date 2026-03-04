drop table if exists persistent_logins;
drop table if exists user_role;
drop table if exists roles;
drop table if exists users;

create table if not exists persistent_logins ( 
  username varchar(100) not null, 
  series varchar(64) primary key, 
  token varchar(64) not null, 
  last_used timestamp not null
);

create table if not exists users (
  id bigint not null auto_increment primary key,
   email varchar(255) not null unique,
   password varchar(255) not null,
   name varchar(255) not null
);

INSERT INTO users (id, email, password, name) VALUES
(1, 'admin@gmail.com', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'Admin'),
(3, 'user@gmail.com', '$2a$10$ByIUiNaRfBKSV6urZoBBxe4UbJ/sS6u1ZaPORHF9AtNWAuVPVz1by', 'User');

create table if not exists roles (
  id bigint not null auto_increment primary key,
  name varchar(255) not null
);

INSERT INTO roles (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_ACTUATOR'),
(3, 'ROLE_USER')
;

create table if not exists user_role (
  id bigint not null auto_increment primary key,
  user_id bigint not null,
  role_id bigint not null,
  foreign key (user_id) references users(id),
  foreign key (role_id) references roles(id)
);

insert into user_role(user_id, role_id) values
(1,1),
(1,2),
(1,3),
(3,2)
;
