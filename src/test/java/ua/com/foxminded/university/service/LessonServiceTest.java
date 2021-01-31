package ua.com.foxminded.university.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.exceptions.*;
import ua.com.foxminded.university.model.*;
import ua.com.foxminded.university.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private ClassroomRepository classroomRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private LessonTimeRepository lessonTimeRepository;
    @InjectMocks
    private LessonService lessonService;

    @Test
    public void shouldGetLesson() {
        Lesson lesson = getLesson();

        when(lessonRepository.findById(1)).thenReturn(Optional.of(lesson));

        assertEquals(lesson, lessonService.get(1));
    }

    @Test
    public void shouldThrowExceptionWhenFindLessonByNonExistentId() {
        when(lessonRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(WebNotFoundException.class, () -> lessonService.get(0));
    }

    @Test
    public void shouldGetAllLessons() {
        List<Lesson> lessons = new ArrayList<>();
        lessons.add(getLesson());
        lessons.add(getLesson());

        when(lessonRepository.findAll()).thenReturn(lessons);

        assertEquals(lessons, lessonService.getAll());
    }

    @Test
    public void shouldDeleteLesson() {
        lessonService.delete(1);

        verify(lessonRepository).deleteById(1);
    }

    @Test
    public void shouldThrowExceptionWhenCreateWithWeekend() {
        Lesson lesson = getLesson();
        lesson.setDate(LocalDate.of(2020, 9, 6));

        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, true);

        assertThrows(NotWeekdayLessonException.class, () -> lessonService.create(lesson));

        verify(lessonRepository, never()).save(lesson);
    }

    @Test
    public void shouldThrowExceptionWhenWhenUpdateWithWeekend() {
        Lesson lesson = getLesson();
        lesson.setDate(LocalDate.of(2020, 9, 6));

        whenGetOneLesson(lesson);
        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, true);

        assertThrows(NotWeekdayLessonException.class, () -> lessonService.update(lesson));

        verify(lessonRepository, never()).save(lesson);
    }

    @Test
    public void shouldThrowExceptionWhenCreateWithSmallClassroom() {
        Lesson lesson = getLesson();

        whenRefreshEntities(lesson, 21);
        whenRefreshTeacher(lesson, true);

        lesson.setGroup(getGroup(22));
        assertThrows(NotEnoughClassroomCapacityException.class, () -> lessonService.create(lesson));

        verify(lessonRepository, never()).save(lesson);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateWithSmallClassroom() {
        Lesson lesson = getLesson();
        lesson.setGroup(getGroup(22));

        whenGetOneLesson(lesson);
        whenRefreshEntities(lesson, 21);
        whenRefreshTeacher(lesson, true);

        assertThrows(NotEnoughClassroomCapacityException.class, () -> lessonService.update(lesson));

        verify(lessonRepository, never()).save(lesson);

    }

    @Test
    public void shouldThrowExceptionWhenCreateWithTeacherDoNotTeachSubject() {
        Lesson lesson = getLesson();
        lesson.getTeacher().getSubjects().clear();

        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, false);

        assertThrows(TeacherCanNotTeachSubjectException.class, () -> lessonService.create(lesson));

        verify(lessonRepository, never()).save(lesson);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateWithTeacherDoNotTeachSubject() {
        Lesson lesson = getLesson();
        lesson.getTeacher().getSubjects().clear();

        whenGetOneLesson(lesson);
        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, false);

        assertThrows(TeacherCanNotTeachSubjectException.class, () -> lessonService.update(lesson));

        verify(lessonRepository, never()).save(lesson);
    }

    @Test
    public void shouldThrowExceptionWhenCreateWithOverlappingTeacher() {
        Lesson lesson = getLesson();

        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, true);
        when(lessonRepository.getByTeacherAndDateAndLessonTime(any(),
                any(), any())).thenReturn(new Lesson());

        assertThrows(TeacherOverlapsException.class, () -> lessonService.create(lesson));

        verify(lessonRepository, never()).save(lesson);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateWithOverlappingTeacher() {
        Lesson lesson = getLesson();

        whenGetOneLesson(lesson);
        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, true);
        when(lessonRepository.getByTeacherAndDateAndLessonTime(any(),
                any(), any())).thenReturn(new Lesson());

        assertThrows(TeacherOverlapsException.class, () -> lessonService.update(lesson));

        verify(lessonRepository, never()).save(lesson);
    }

    @Test
    public void shouldUpdateWhenTeacherOverlapsWithUpdatingLesson() throws DomainException {
        Lesson lesson = getLesson();
        Lesson existingLesson = new Lesson();
        existingLesson.setId(1);

        whenGetOneLesson(lesson);
        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, true);
        when(lessonRepository.getByTeacherAndDateAndLessonTime(any(),
                any(), any())).thenReturn(lesson);

        lessonService.update(lesson);

        verify(lessonRepository).save(lesson);
    }

    @Test
    public void shouldThrowExceptionWhenCreateWithOverlappingClassroom() {
        Lesson lesson = getLesson();
        lesson.setGroup(getGroup(20));

        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, true);
        when(lessonRepository.getByTeacherAndDateAndLessonTime(any(),
                any(), any())).thenReturn(null);
        when(lessonRepository.getByClassroomAndDateAndLessonTime(any(),
                any(), any())).thenReturn(new Lesson());

        assertThrows(ClassroomOverlapsException.class, () -> lessonService.create(lesson));

        verify(lessonRepository, never()).save(lesson);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateWithOverlappingClassroom() {
        Lesson lesson = getLesson();
        lesson.setGroup(getGroup(20));

        whenGetOneLesson(lesson);
        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, true);
        when(lessonRepository.getByTeacherAndDateAndLessonTime(any(),
                any(), any())).thenReturn(null);
        when(lessonRepository.getByClassroomAndDateAndLessonTime(any(),
                any(), any())).thenReturn(new Lesson());

        assertThrows(ClassroomOverlapsException.class, () -> lessonService.update(lesson));

        verify(lessonRepository, never()).save(lesson);
    }

    @Test
    public void shouldUpdateWhenClassroomOverlapsWithUpdatingLesson() throws DomainException {
        Lesson lesson = getLesson();
        lesson.setGroup(getGroup(20));
        Lesson existingLesson = new Lesson();
        existingLesson.setId(1);

        whenGetOneLesson(lesson);
        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, true);
        when(lessonRepository.getByTeacherAndDateAndLessonTime(any(),
                any(), any())).thenReturn(null);
        when(lessonRepository.getByClassroomAndDateAndLessonTime(any(),
                any(), any())).thenReturn(lesson);

        lessonService.update(lesson);

        verify(lessonRepository).save(lesson);
    }

    @Test
    public void shouldThrowExceptionWhenCreateWithOverlappingGroup() {
        Lesson lesson = getLesson();
        lesson.setGroup(getGroup(20));

        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, true);
        when(lessonRepository.getByTeacherAndDateAndLessonTime(any(),
                any(), any())).thenReturn(null);
        when(lessonRepository.getByClassroomAndDateAndLessonTime(any(),
                any(), any())).thenReturn(null);
        when(lessonRepository.getByGroupAndDateAndLessonTime(any(),
                any(), any())).thenReturn(new Lesson());

        assertThrows(GroupOverlapsException.class, () -> lessonService.create(lesson));

        verify(lessonRepository, never()).save(lesson);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateWithOverlappingGroup() {
        Lesson lesson = getLesson();
        lesson.setGroup(getGroup(20));

        whenGetOneLesson(lesson);
        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, true);
        when(lessonRepository.getByTeacherAndDateAndLessonTime(any(),
                any(), any())).thenReturn(null);
        when(lessonRepository.getByClassroomAndDateAndLessonTime(any(),
                any(), any())).thenReturn(null);
        when(lessonRepository.getByGroupAndDateAndLessonTime(any(),
                any(), any())).thenReturn(new Lesson());

        assertThrows(GroupOverlapsException.class, () -> lessonService.update(lesson));

        verify(lessonRepository, never()).save(lesson);
    }

    @Test
    public void shouldUpdateWhenGroupOverlapsWithUpdatingLesson() throws DomainException {
        Lesson lesson = getLesson();
        lesson.setGroup(getGroup(20));
        Lesson existingLesson = new Lesson();
        existingLesson.setId(1);

        whenGetOneLesson(lesson);
        whenRefreshEntities(lesson, 0);
        whenRefreshTeacher(lesson, true);
        when(lessonRepository.getByTeacherAndDateAndLessonTime(any(),
                any(), any())).thenReturn(null);
        when(lessonRepository.getByClassroomAndDateAndLessonTime(any(),
                any(), any())).thenReturn(null);
        when(lessonRepository.getByGroupAndDateAndLessonTime(any(),
                any(), any())).thenReturn(lesson);

        lessonService.update(lesson);

        verify(lessonRepository).save(lesson);
    }

    private Lesson getLesson() {
        Lesson lesson = new Lesson();
        lesson.setId(1);
        lesson.setDate(LocalDate.of(2020, 9, 4));
        lesson.setLessonTime(new LessonTime(1));
        lesson.setClassroom(new Classroom(1));
        lesson.setSubject(new Subject(1));
        lesson.setTeacher(new Teacher(1));
        lesson.setGroup(getGroup(0));

        return lesson;
    }

    private Group getGroup(int studentsAmount) {
        Group group = new Group(1, "group", 1);
        group.setVersion(0L);
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < studentsAmount; i++) {
            students.add(new Student());
        }
        group.setStudents(students);
        return group;
    }

    private void whenRefreshEntities(Lesson lesson, int studentsAmount) {
        Classroom classroom = new Classroom(1, "classroom", 20);
        classroom.setVersion(0L);

        Subject subject = new Subject("math");
        subject.setId(1);
        subject.setVersion(0L);

        LessonTime lessonTime = new LessonTime(1, LocalTime.of(8, 0), LocalTime.of(9, 30));
        lessonTime.setVersion(0L);


        when(classroomRepository.getOne(lesson.getClassroom().getId())).thenReturn(classroom);
        when(groupRepository.getOne(lesson.getGroup().getId())).thenReturn(getGroup(studentsAmount));
        when(subjectRepository.getOne(lesson.getSubject().getId())).thenReturn(subject);
        when(lessonTimeRepository.getOne(lesson.getLessonTime().getId())).thenReturn(lessonTime);
    }

    private void whenRefreshTeacher(Lesson lesson, boolean teaching) {
        Teacher teacher = new Teacher();
        teacher.setVersion(0L);
        teacher.setId(1);
        if (teaching) {
            teacher.getSubjects().add(new Subject(1, "math"));
        }

        when(teacherRepository.getOne(lesson.getTeacher().getId())).thenReturn(teacher);
    }

    private void whenGetOneLesson(Lesson lesson) {
        when(lessonRepository.getOne(lesson.getId())).thenReturn(getLesson());
    }
}
