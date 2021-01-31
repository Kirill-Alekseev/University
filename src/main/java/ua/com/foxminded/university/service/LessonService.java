package ua.com.foxminded.university.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.exceptions.*;
import ua.com.foxminded.university.model.*;
import ua.com.foxminded.university.repository.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

@Slf4j
@Service
@Transactional(rollbackFor = DomainException.class)
public class LessonService {

    private LessonRepository lessonRepository;
    private ClassroomRepository classroomRepository;
    private TeacherRepository teacherRepository;
    private GroupRepository groupRepository;
    private SubjectRepository subjectRepository;
    private LessonTimeRepository lessonTimeRepository;

    public LessonService(LessonRepository lessonRepository, ClassroomRepository classroomRepository,
                         TeacherRepository teacherRepository, StudentRepository studentRepository,
                         GroupRepository groupRepository, SubjectRepository subjectRepository,
                         LessonTimeRepository lessonTimeRepository) {
        this.lessonRepository = lessonRepository;
        this.classroomRepository = classroomRepository;
        this.teacherRepository = teacherRepository;
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
        this.lessonTimeRepository = lessonTimeRepository;
    }

    public List<Lesson> getAll() {
        log.debug("Get all lessons");
        return lessonRepository.findAll();
    }

    public List<Lesson> getAll(Pageable pageable) {
        log.debug("Get all lessons");
        return lessonRepository.findAll(pageable).getContent();
    }

    public Lesson get(int id) {
        log.debug("Get lesson by id: {}", id);
        return lessonRepository.findById(id)
                .orElseThrow(() -> new WebNotFoundException("Can't find lesson by passed id: %d", id));
    }

    public void create(Lesson lesson) throws DomainException {
        refreshEntities(lesson);
        verifyLessonCorrectToBeUploaded(lesson);

        lessonRepository.save(lesson);
        log.debug("Create lesson {}", lesson);
    }

    public Lesson update(Lesson lesson) throws DomainException {
        Lesson updated = lessonRepository.getOne(lesson.getId());
        updated.setTeacher(lesson.getTeacher());
        updated.setClassroom(lesson.getClassroom());
        updated.setGroup(lesson.getGroup());
        updated.setSubject(lesson.getSubject());
        updated.setLessonTime(lesson.getLessonTime());
        updated.setDate(lesson.getDate());
        refreshEntities(updated);
        verifyLessonCorrectToBeUploaded(updated);

        lessonRepository.save(updated);
        log.debug("Update lesson {}", updated);
        return updated;
    }

    public void delete(int id) {
        log.debug("Delete lesson by id: {}", id);
        lessonRepository.deleteById(id);
    }

    public List<Lesson> getByGroupBetweenTwoDates(Group group, LocalDate fromDate, LocalDate toDate) {
        log.debug("Get lessons by group: {} between: {} and {}", group, fromDate, toDate);
        return lessonRepository.getByGroupAndDateBetween(group, fromDate, toDate);
    }

    public List<Lesson> getByGroupAndDate(Group group, LocalDate date) {
        log.debug("Get lessons by group: {} and date: {}", group, date);
        return lessonRepository.getByGroupAndDate(group, date);
    }

    public List<Lesson> getByTeacherBetweenTwoDates(Teacher teacher, LocalDate fromDate, LocalDate toDate) {
        log.debug("Get lessons by teacher: {} between: {} and {}", teacher, fromDate, toDate);
        return lessonRepository.getByTeacherAndDateBetween(teacher, fromDate, toDate);
    }

    public List<Lesson> getByTeacherAndDate(Teacher teacher, LocalDate date) {
        log.debug("Get lessons by teacher: {} and date: {}", teacher, date);
        return lessonRepository.getByTeacherAndDate(teacher, date);
    }

    private void verifyLessonCorrectToBeUploaded(Lesson lesson) throws DomainException {
        verifyWeekday(lesson);
        verifySuitableClassroom(lesson);
        verifyTeacherTeachSubject(lesson);
        verifyTeacherFree(lesson);
        verifyClassroomFree(lesson);
        verifyGroupFree(lesson);
    }

    private void verifyWeekday(Lesson lesson) throws NotWeekdayLessonException {
        DayOfWeek dayOfWeek = lesson.getDate().getDayOfWeek();
        if (dayOfWeek == SUNDAY || dayOfWeek == SATURDAY)
            throw new NotWeekdayLessonException("Passed date: %s is weekend", lesson.getDate());
    }

    private void verifySuitableClassroom(Lesson lesson) throws NotEnoughClassroomCapacityException {
        int studentsQuantity = lesson.getGroup().getStudents().size();
        Classroom classroom = lesson.getClassroom();
        if (classroom.getCapacity() < studentsQuantity)
            throw new NotEnoughClassroomCapacityException("Capacity: %d less than students number: %d",
                    classroom.getCapacity(), studentsQuantity);
    }

    private void verifyTeacherTeachSubject(Lesson lesson) throws TeacherCanNotTeachSubjectException {
        Teacher teacher = lesson.getTeacher();
        boolean teacherUnableToTeach = teacher.getSubjects().stream()
                .noneMatch(subject -> lesson.getSubject().equals(subject));
        if (teacherUnableToTeach)
            throw new TeacherCanNotTeachSubjectException("Teacher does not teach subject with id = %d",
                    lesson.getSubject().getId());
    }

    private void verifyTeacherFree(Lesson lesson) throws TeacherOverlapsException {
        Lesson teacherLesson = getTeacherLesson(lesson);
        if (teacherLesson == null)
            return;
        if (isOtherLesson(lesson, teacherLesson))
            throw new TeacherOverlapsException("Teacher is busy at this time");
    }

    private void verifyClassroomFree(Lesson lesson) throws ClassroomOverlapsException {
        Lesson classroomLesson = getClassroomLesson(lesson);
        if (classroomLesson == null)
            return;
        if (isOtherLesson(lesson, classroomLesson))
            throw new ClassroomOverlapsException("Classroom is occupied at this time");
    }

    private void verifyGroupFree(Lesson lesson) throws GroupOverlapsException {
        Lesson groupLesson = getGroupLesson(lesson);
        if (groupLesson == null)
            return;
        if (isOtherLesson(lesson, groupLesson))
            throw new GroupOverlapsException("Group is busy at this time");
    }

    private Lesson getTeacherLesson(Lesson lesson) {
        return lessonRepository.getByTeacherAndDateAndLessonTime(lesson.getTeacher(),
                lesson.getDate(), lesson.getLessonTime());
    }

    private Lesson getClassroomLesson(Lesson lesson) {
        return lessonRepository.getByClassroomAndDateAndLessonTime(lesson.getClassroom(),
                lesson.getDate(), lesson.getLessonTime());
    }

    private Lesson getGroupLesson(Lesson lesson) {
        return lessonRepository.getByGroupAndDateAndLessonTime(lesson.getGroup(),
                lesson.getDate(), lesson.getLessonTime());
    }

    private boolean isOtherLesson(Lesson lessonToUpdate, Lesson lesson) {
        return !lessonToUpdate.getId().equals(lesson.getId());
    }

    private void refreshEntities(Lesson lesson) {
        Teacher teacher = teacherRepository.getOne(lesson.getTeacher().getId());
        Classroom classroom = classroomRepository.getOne(lesson.getClassroom().getId());
        Group group = groupRepository.getOne(lesson.getGroup().getId());
        Subject subject = subjectRepository.getOne(lesson.getSubject().getId());
        LessonTime lessonTime = lessonTimeRepository.getOne(lesson.getLessonTime().getId());

        lesson.setTeacher(teacher);
        lesson.setClassroom(classroom);
        lesson.setGroup(group);
        lesson.setSubject(subject);
        lesson.setLessonTime(lessonTime);
    }
}
