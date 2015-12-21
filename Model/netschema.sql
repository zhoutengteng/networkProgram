drop database if exists NETWORK;
create database NETWORK CHARACTER SET 'utf8'   COLLATE 'utf8_general_ci';  
use NETWORK;


create table tbl_user (
    ID bigint not null auto_increment primary key,
    NAME varchar(16) not null,
    AGE INT,
    PASSWORD varchar(20),
    ADDRESS varchar(255)
) ENGINE =INNODB DEFAULT CHARSET=utf8;


create table tbl_curConection (
    ID INT not null auto_increment primary key,
    IP varchar(50) not null,
    PORT INT not null 
) ENGINE =INNODB DEFAULT CHARSET=utf8;

insert into tbl_user(ID, NAME, AGE, PASSWORD,  ADDRESS) values(1, 'admin', 23, "admin",  '北京'); 


select * from tbl_user;
