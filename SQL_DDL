# table 생성
create table user(
user_id int auto_increment primary key,
user_name varchar(15),
password varchar(20),
name varchar(20),
email varchar(25),
birthday datetime,
gender varchar(15),
introduction varchar(100),
created_at datetime,
updated_at datetime
);
desc user;

create table post(
post_id int primary key,
user_id int,
test text,
tag varchar(20),
created_at datetime,
updated_at datetime,
foreign key (user_id) references user(user_id)
);
desc post;

create table likes(
user_id int,
post_id int,
primary key (user_id, post_id),
foreign key (user_id) references user(user_id),
foreign key (post_id) references post(post_id)
);
desc likes;

create table comment(
comment_id int primary key,
post_id int,
user_id int,
comment varchar(45),
created_at datetime,
updated_at datetime,
foreign key (post_id) references post(post_id),
foreign key (user_id) references user(user_id)
);
desc comment;

create table following(
user_id int,
following_id int,
primary key (user_id, following_id),
foreign key (user_id) references user (user_id));
desc following;

create table follower(
user_id int,
follower_id int,
primary key (user_id, follower_id),
foreign key (user_id) references user (user_id));
desc follower;

show tables;
