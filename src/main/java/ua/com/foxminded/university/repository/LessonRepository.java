package ua.com.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.foxminded.university.model.*;

import java.time.LocalDate;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {

    List<Lesson> getByGroupAndDateBetween(Group group, LocalDate fromDate, LocalDate toDate);

    List<Lesson> getByGroupAndDate(Group group, LocalDate date);

    List<Lesson> getByTeacherAndDateBetween(Teacher teacher, LocalDate fromDate, LocalDate toDate);

    List<Lesson> getByTeacherAndDate(Teacher teacher, LocalDate date);

    Lesson getByTeacherAndDateAndLessonTime(Teacher teacher, LocalDate date, LessonTime lessonTime);

    Lesson getByGroupAndDateAndLessonTime(Group group, LocalDate date, LessonTime lessonTime);

    Lesson getByClassroomAndDateAndLessonTime(Classroom classroom, LocalDate date, LessonTime lessonTime);
}

