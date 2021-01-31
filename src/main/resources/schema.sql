CREATE TABLE IF NOT EXISTS faculties
(
    id      int PRIMARY KEY auto_increment,
    name    VARCHAR(100) NOT NULL UNIQUE,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS specialties
(
    id         int PRIMARY KEY auto_increment,
    name       VARCHAR(100) NOT NULL UNIQUE,
    faculty_id INT REFERENCES faculties (id) ON DELETE CASCADE ON UPDATE CASCADE,
    version    BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS groups
(
    id           int PRIMARY KEY auto_increment,
    name         VARCHAR(10) NOT NULL UNIQUE,
    course       INT,
    specialty_id INT REFERENCES specialties (id) ON DELETE CASCADE ON UPDATE CASCADE,
    version      BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS students
(
    id           int PRIMARY KEY auto_increment,
    first_name   VARCHAR(60) NOT NULL,
    last_name    VARCHAR(60) NOT NULL,
    group_id     INT REFERENCES groups (id) ON DELETE CASCADE ON UPDATE CASCADE,
    email        VARCHAR(100),
    phone_number VARCHAR(20),
    birth_date   DATE,
    version      BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS cathedras
(
    id         int PRIMARY KEY auto_increment,
    name       VARCHAR(100) NOT NULL UNIQUE,
    faculty_id INT REFERENCES faculties (id) ON DELETE CASCADE ON UPDATE CASCADE,
    version    BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS teachers
(
    id         int PRIMARY KEY auto_increment,
    first_name VARCHAR(60) NOT NULL,
    last_name  VARCHAR(60) NOT NULL,
    version    BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS cathedras_teachers
(
    cathedra_id INT NOT NULL REFERENCES cathedras (id) ON DELETE CASCADE ON UPDATE CASCADE,
    teacher_id  INT NOT NULL REFERENCES teachers (id) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint cathedras_teachers_pkey PRIMARY KEY (cathedra_id, teacher_id)
);

CREATE TABLE IF NOT EXISTS subjects
(
    id          int PRIMARY KEY auto_increment,
    name        VARCHAR(100) NOT NULL UNIQUE,
    cathedra_id INT          NOT NULL REFERENCES cathedras (id) ON DELETE CASCADE ON UPDATE CASCADE,
    version     BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS teachers_subjects
(
    teacher_id INT NOT NULL REFERENCES teachers (id) ON DELETE CASCADE ON UPDATE CASCADE,
    subject_id INT NOT NULL REFERENCES subjects (id) ON DELETE CASCADE ON UPDATE CASCADE,
    constraint teachers_subjects_pkey PRIMARY KEY (teacher_id, subject_id)
);

CREATE TABLE IF NOT EXISTS classrooms
(
    id       int PRIMARY KEY auto_increment,
    name     VARCHAR(10) NOT NULL UNIQUE,
    capacity INT,
    version  BIGINT DEFAULT 0

);

CREATE TABLE IF NOT EXISTS lesson_time
(
    id         int PRIMARY KEY auto_increment,
    begin_time TIME NOT NULL,
    end_time   TIME NOT NULL,
    version    BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS lessons
(
    id             int PRIMARY KEY auto_increment,
    subject_id     INT  NOT NULL REFERENCES subjects (id) ON DELETE CASCADE ON UPDATE CASCADE,
    classroom_id   INT  NOT NULL REFERENCES classrooms (id) ON DELETE CASCADE ON UPDATE CASCADE,
    teacher_id     INT  NOT NULL REFERENCES teachers (id) ON DELETE CASCADE ON UPDATE CASCADE,
    group_id       INT  NOT NULL REFERENCES groups (id) ON DELETE CASCADE ON UPDATE CASCADE,
    lesson_date    DATE NOT NULL,
    lesson_time_id INT  NOT NULL REFERENCES lesson_time (id) ON DELETE CASCADE ON UPDATE CASCADE,
    version        BIGINT DEFAULT 0
);
