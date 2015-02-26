drop database if exists BarnyardTestDB;
create database BarnyardTestDB;
use BarnyardTestDB;

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
VALUES ('1', 'Василий', 'Пупкин', '12000', '1980-02-02', true),
    ('1', 'Елена', 'Павлова', '15000', '1960-03-03', true);