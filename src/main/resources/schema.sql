DROP TABLE IF EXISTS students;

CREATE TABLE students
(
    id          bigint PRIMARY KEY,
    student_id  int,
    name        VARCHAR(250) NOT NULL,
    group_name  VARCHAR(250) NOT NULL,
    study_level int
);
