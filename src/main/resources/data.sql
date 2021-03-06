INSERT INTO faculties(id, name)
VALUES (1, 'Faculty of Computer Automation');

INSERT INTO specialties(id, name, faculty_id)
VALUES (1, 'Social Sciences', 1),
       (2, 'Engineering and Technology', 1),
       (3, 'Arts and Humanities', 1),
       (4, 'Natural Sciences', 1);

INSERT INTO groups(id, name, course, specialty_id)
VALUES (1, 'PZ-84', 4, 1),
       (2, 'QN-90', 1, 2),
       (3, 'FM-62', 2, 2),
       (4, 'KY-89', 6, 3),
       (5, 'ZK-49', 3, 4);

INSERT INTO students(id, first_name, last_name, group_id, email, phone_number, birth_date)
VALUES (1, 'Ava', 'Harris', 1, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (2, 'Emily', 'Thompson', 1, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (3, 'Mark', 'Moore', 1, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (4, 'Ava', 'Davis', 1, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (5, 'Bella', 'Jackson', 1, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (6, 'Ava', 'Lewis', 2, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (7, 'Noah', 'Wilson', 2, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (8, 'Jacob', 'Taylor', 2, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (9, 'Jacob', 'Garcia', 2, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (10, 'Ava', 'Clark', 2, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (11, 'James', 'Anderson', 3, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (12, 'Emma', 'Moore', 3, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (13, 'Emma', 'Lewis', 3, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (14, 'Emma', 'Martin', 3, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (15, 'Abigail', 'Brown', 3, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (16, 'Ava', 'Smith', 4, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (17, 'Bill', 'Jones', 4, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (18, 'Mia', 'Jackson', 4, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (19, 'Olivia', 'Moore', 4, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (20, 'Sam', 'Smith', 4, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (21, 'Jayden', 'Harris', 5, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (22, 'Sophia', 'Smith', 5, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (23, 'Mark', 'Martin', 5, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (24, 'Emily', 'Lee', 5, 'avaharris@mail.com', '+380111111111', '2000-01-01'),
       (25, 'John', 'Davis', 5, 'avaharris@mail.com', '+380111111111', '2000-01-01');

INSERT INTO cathedras(id, name, faculty_id)
VALUES (1, 'Cathedra of Math', 1),
       (2, 'Cathedra of Art', 1),
       (3, 'Cathedra of History', 1),
       (4, 'Cathedra of Information Technology', 1),
       (5, 'Cathedra of Biology', 1),
       (6, 'Cathedra of Philology', 1),
       (7, 'Cathedra of Philosophy', 1),
       (8, 'Cathedra of Physical Education', 1),
       (9, 'Cathedra of Chemistry', 1);

INSERT INTO teachers(id, first_name, last_name)
VALUES (1, 'Ava', 'Harris'),
       (2, 'Emily', 'Thompson'),
       (3, 'Mark', 'Moore'),
       (4, 'Ava', 'Davis'),
       (5, 'Bella', 'Jackson'),
       (6, 'Ava', 'Lewis'),
       (7, 'Noah', 'Wilson'),
       (8, 'Jacob', 'Taylor'),
       (9, 'Jacob', 'Garcia'),
       (10, 'Ava', 'Clark'),
       (11, 'James', 'Anderson'),
       (12, 'Emma', 'Moore'),
       (13, 'Emma', 'Lewis'),
       (14, 'Emma', 'Martin'),
       (15, 'Abigail', 'Brown'),
       (16, 'Ava', 'Smith'),
       (17, 'Bill', 'Jones'),
       (18, 'Mia', 'Jackson'),
       (19, 'Olivia', 'Moore'),
       (20, 'Sam', 'Smith');

INSERT INTO subjects(id, name, cathedra_id)
VALUES (1, 'Algebra', 1),
       (2, 'Geometry', 1),
       (3, 'Drawing', 2),
       (4, 'Music', 2),
       (5, 'History', 3),
       (6, 'Computer Science', 4),
       (7, 'Biology', 5),
       (8, 'Foreign Language', 6),
       (9, 'Literature', 6),
       (10, 'Philosophy', 7),
       (11, 'Physical Training', 8),
       (12, 'Chemistry', 9);

INSERT INTO classrooms(id, name, capacity)
VALUES (1, '511', 20),
       (2, '644', 20),
       (3, '645', 20),
       (4, '823', 20),
       (5, '990', 20),
       (6, '126', 20),
       (7, '672', 20),
       (8, '124', 20),
       (9, '543', 20),
       (10, '857', 20);

INSERT INTO cathedras_teachers(cathedra_id, teacher_id)
VALUES (8, 1),
       (8, 2),
       (7, 2),
       (4, 3),
       (2, 4),
       (3, 4),
       (6, 5),
       (9, 5),
       (2, 6),
       (1, 7),
       (8, 7),
       (4, 8),
       (7, 8),
       (5, 9),
       (8, 9),
       (8, 10),
       (1, 10),
       (2, 11),
       (5, 11),
       (4, 12),
       (7, 12),
       (1, 13),
       (7, 14),
       (5, 14),
       (7, 15),
       (1, 16),
       (2, 16),
       (5, 17),
       (9, 17),
       (1, 18),
       (2, 18),
       (4, 19),
       (9, 19),
       (1, 20),
       (6, 20);

INSERT INTO teachers_subjects(teacher_id, subject_id)
VALUES (1, 11),
       (2, 11),
       (2, 10),
       (3, 6),
       (4, 5),
       (4, 4),
       (5, 8),
       (5, 12),
       (6, 3),
       (7, 2),
       (7, 11),
       (8, 10),
       (8, 6),
       (9, 7),
       (9, 11),
       (10, 1),
       (10, 11),
       (11, 4),
       (11, 7),
       (12, 10),
       (12, 6),
       (13, 1),
       (14, 10),
       (14, 7),
       (15, 10),
       (16, 3),
       (16, 1),
       (17, 12),
       (17, 7),
       (18, 4),
       (18, 1),
       (19, 12),
       (19, 6),
       (20, 2),
       (20, 9);

INSERT INTO lesson_time(id, begin_time, end_time)
VALUES (1, '8:00', '9:30'),
       (2, '9:45', '11:15'),
       (3, '11:30', '13:00'),
       (4, '13:15', '14:45'),
       (5, '15:00', '16:30'),
       (6, '16:45', '18:15');

INSERT INTO lessons (id, subject_id, classroom_id, teacher_id, group_id, lesson_date, lesson_time_id)
VALUES (1, 1, 1, 1, 1, '2020-09-09', 1),
       (2, 2, 2, 2, 2, '2020-09-09', 2),
       (3, 3, 3, 3, 3, '2020-09-09', 3),
       (4, 4, 4, 4, 4, '2020-09-09', 4),
       (5, 5, 5, 5, 5, '2020-09-09', 5),
       (6, 6, 6, 6, 1, '2020-09-09', 6),
       (7, 7, 7, 7, 2, '2020-09-09', 1),
       (8, 8, 8, 8, 3, '2020-09-09', 2),
       (9, 9, 9, 9, 4, '2020-09-09', 3),
       (10, 10, 10, 10, 5, '2020-09-09', 4),
       (11, 11, 1, 11, 1, '2020-09-09', 5),
       (12, 12, 2, 12, 2, '2020-09-09', 6),
       (13, 1, 1, 1, 1, '2020-09-10', 1),
       (14, 1, 1, 1, 1, '2020-09-11', 1),
       (15, 1, 1, 1, 1, '2020-09-12', 1),
       (16, 1, 1, 1, 1, '2020-09-13', 1),
       (17, 1, 1, 1, 1, '2020-09-14', 1),
       (18, 1, 1, 1, 1, '2020-09-15', 1),
       (19, 1, 1, 1, 1, '2020-09-16', 1),
       (20, 1, 1, 1, 1, '2020-09-17', 1),
       (21, 1, 1, 1, 1, '2020-09-18', 1),
       (22, 1, 1, 1, 1, '2020-09-19', 1),
       (23, 1, 1, 1, 1, '2020-09-20', 1);
