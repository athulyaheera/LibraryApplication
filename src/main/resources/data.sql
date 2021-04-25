--create table book (id int auto_increment primary key, name varchar,bookcode varchar int not null);
insert into book (id , name , isbn ) VALUES (101,'abc','def');
insert into book (id , name , isbn ) VALUES (102,'abc','def');
--create table users (id int auto_increment primary key, name varchar);
insert into users (id , name ) VALUES (101,'abc');

insert into register (id , boook_id, action, borrowedUser ) VALUES (101,102,"BORRWORED",101);

--create table library (id int auto_increment primary key, bookId int not null, isBorrowed varchar, userId int);
--insert into book (1,1,"abc");

