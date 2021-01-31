INSERT INTO faculties(ID, NAME)
VALUES (1, 'Faculty of Computer Automation'),
       (2, 'Information Security Institute');

INSERT INTO cathedras (Id, NAME, FACULTY_ID)
VALUES (1, 'infotech', 1),
       (2, 'math', 2);

INSERT INTO subjects (ID, NAME, CATHEDRA_ID)
VALUES (1, 'math', 1),
       (2, 'informatics', 1);

INSERT INTO teachers (ID, FIRST_NAME, LAST_NAME)
VALUES (1, 'mykola', 'baranov'),
       (2, 'anatoliy', 'nikolenko');

INSERT INTO teachers_subjects (TEACHER_ID, SUBJECT_ID)
VALUES (1, 1),
       (1, 2);

INSERT INTO cathedras_teachers (CATHEDRA_ID, TEACHER_ID)
VALUES (1, 1);

INSERT INTO specialties (ID, NAME, FACULTY_ID)
VALUES (1, 'CS', 1),
       (2, 'SE', 1);

INSERT INTO groups (ID, NAME, COURSE, SPECIALTY_ID)
VALUES (1, 'ai-183', 1, 1),
       (2, 'ai-182', 1, 1);

INSERT INTO classrooms(ID, NAME, CAPACITY)
VALUES (1, '111', 10),
       (2, '222', 10);

INSERT INTO students (ID, FIRST_NAME, LAST_NAME, GROUP_ID)
VALUES (1, 'ivan', 'petrov', 1),
       (2, 'petr', 'ivanov', 1);

INSERT INTO lesson_time(ID, BEGIN_TIME, END_TIME)
values (1, '08:00:00', '09:30:00'),
       (2, '09:45:00', '11:15:00');

INSERT INTO lessons (ID, SUBJECT_ID, CLASSROOM_ID, TEACHER_ID, GROUP_ID, LESSON_DATE, LESSON_TIME_ID)
VALUES (1, 1, 1, 1, 1, '2020-09-03', 1),
       (2, 1, 1, 1, 1, '2020-09-03', 2);
