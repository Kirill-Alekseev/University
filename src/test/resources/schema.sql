DROP TABLE IF EXISTS faculties CASCADE;
DROP TABLE IF EXISTS specialties CASCADE;
DROP TABLE IF EXISTS cathedras CASCADE;
DROP TABLE IF EXISTS teachers CASCADE;
DROP TABLE IF EXISTS cathedras_teachers CASCADE;
DROP TABLE IF EXISTS subjects CASCADE;
DROP TABLE IF EXISTS teachers_subjects CASCADE;
DROP TABLE IF EXISTS classrooms CASCADE;
DROP TABLE IF EXISTS lessons CASCADE;
DROP TABLE IF EXISTS groups CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS lesson_time CASCADE;

CREATE TABLE faculties
(
    id   int PRIMARY KEY auto_increment,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE specialties
(
    id         int PRIMARY KEY auto_increment,
    name       VARCHAR(100) NOT NULL UNIQUE,
    faculty_id INT REFERENCES faculties (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE groups
(
    id           int PRIMARY KEY auto_increment,
    name         VARCHAR(10) NOT NULL UNIQUE,
    course       INT,
    specialty_id INT REFERENCES specialties (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE students
(
    id         int PRIMARY KEY auto_increment,
    first_name VARCHAR(60) NOT NULL,
    last_name  VARCHAR(60) NOT NULL,
    group_id   INT REFERENCES groups (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE cathedras
(
    id         int PRIMARY KEY auto_increment,
    name       VARCHAR(100) NOT NULL UNIQUE,
    faculty_id INT REFERENCES faculties (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE teachers
(
    id         int PRIMARY KEY auto_increment,
    first_name VARCHAR(60) NOT NULL,
    last_name  VARCHAR(60) NOT NULL
);

CREATE TABLE cathedras_teachers
(
    cathedra_id INT NOT NULL REFERENCES cathedras (id) ON DELETE CASCADE ON UPDATE CASCADE,
    teacher_id  INT NOT NULL REFERENCES teachers (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT cathedras_teachers_pkey PRIMARY KEY (cathedra_id, teacher_id)
);

CREATE TABLE subjects
(
    id          int PRIMARY KEY auto_increment,
    name        VARCHAR(100) NOT NULL UNIQUE,
    cathedra_id INT          NOT NULL REFERENCES cathedras (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE teachers_subjects
(
    teacher_id INT NOT NULL REFERENCES teachers (id) ON DELETE CASCADE ON UPDATE CASCADE,
    subject_id INT NOT NULL REFERENCES subjects (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT teachers_subjects_pkey PRIMARY KEY (teacher_id, subject_id)
);

CREATE TABLE classrooms
(
    id       int PRIMARY KEY auto_increment,
    name     VARCHAR(10) NOT NULL,
    capacity INT
);

CREATE TABLE lesson_time
(
    id         int PRIMARY KEY auto_increment,
    begin_time TIME NOT NULL,
    end_time   TIME NOT NULL
);

CREATE TABLE lessons
(
    id             int PRIMARY KEY auto_increment,
    subject_id     INT  NOT NULL REFERENCES subjects (id) ON DELETE CASCADE ON UPDATE CASCADE,
    classroom_id   INT  NOT NULL REFERENCES classrooms (id) ON DELETE CASCADE ON UPDATE CASCADE,
    teacher_id     INT  NOT NULL REFERENCES teachers (id) ON DELETE CASCADE ON UPDATE CASCADE,
    group_id       INT  NOT NULL REFERENCES groups (id) ON DELETE CASCADE ON UPDATE CASCADE,
    lesson_date    DATE NOT NULL,
    lesson_time_id INT  NOT NULL REFERENCES lesson_time (id) ON DELETE CASCADE ON UPDATE CASCADE
);
