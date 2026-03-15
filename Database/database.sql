CREATE DATABASE todolist;
USE todolist;


CREATE TABLE `todolist`.`todoss` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `Todo` TEXT(255) NULL,
  `Description` TEXT(255) NULL,
  `Deadline` DATE NULL,
  `Priority` INT NULL,
  `Status` INT NULL,
  `Tag` TEXT(255) NULL,
  PRIMARY KEY (`id`));

ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'maxdev789';
-- add user for test
CREATE USER 'demo'@'%' IDENTIFIED BY 'maxdev789'; 
GRANT ALL PRIVILEGES ON mydatabase.* TO 'demo'@'%';
-- 
FLUSH PRIVILEGES;

