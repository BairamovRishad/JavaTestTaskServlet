drop database if exists BarnyardDB;
create database BarnyardDB;
use BarnyardDB;

CREATE TABLE department (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(30) NOT NULL,
  PRIMARY KEY (id)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

CREATE TABLE employee (
  id BIGINT NOT NULL AUTO_INCREMENT,
  department_id INT NOT NULL DEFAULT 1,
  first_name VARCHAR(30) NOT NULL,
  last_name VARCHAR(30) NOT NULL,
  salary DOUBLE(15 , 2 ) UNSIGNED NOT NULL,
  birthdate DATE NOT NULL,
  active BOOLEAN DEFAULT TRUE,
  PRIMARY KEY (id),
  FOREIGN KEY (department_id)
  REFERENCES department (id)
    ON DELETE CASCADE
)  ENGINE=INNODB DEFAULT CHARSET=UTF8;

INSERT INTO department (name)
VALUES ('Не определено'),
  ('Бухгалтерия');

INSERT INTO employee (department_id, first_name, last_name, salary, birthdate, active)
VALUES ('1', 'Мария', 'Иванова', '12000', '1980-02-02', true),
  ('1', 'Елена', 'Павлова', '15000', '1960-03-03', true),
  ('1', 'Иосиф', 'Джугашвили', '25000', '1953-03-05', false),
  ('1', 'Василий', 'Пупкин', '20000', '1991-04-03', true);

/*-----------------------------------------------------------------------------------*/

CREATE TABLE users (
  username VARCHAR(45) NOT NULL,
  password VARCHAR(45) NOT NULL,
  enabled TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (username)
);

CREATE TABLE user_roles (
  user_role_id INT(11) NOT NULL AUTO_INCREMENT,
  username VARCHAR(45) NOT NULL,
  role VARCHAR(45) NOT NULL,
  PRIMARY KEY (user_role_id),
  UNIQUE KEY uni_username_role (role , username),
  KEY fk_username_idx (username),
  CONSTRAINT fk_username FOREIGN KEY (username)
  REFERENCES users (username)
);

CREATE TABLE persistent_logins (
  username VARCHAR(64) NOT NULL,
  series VARCHAR(64) NOT NULL,
  token VARCHAR(64) NOT NULL,
  last_used TIMESTAMP NOT NULL,
  PRIMARY KEY (series)
);

INSERT INTO users(username,password,enabled)
VALUES ('anna','qwerty', TRUE),
  ('bob','qwerty', TRUE);

INSERT INTO user_roles (username, role)
VALUES ('anna', 'ROLE_READER'),
  ('bob', 'ROLE_READER'),
  ('bob', 'ROLE_EDITOR');
