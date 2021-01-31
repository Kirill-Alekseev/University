package ua.com.foxminded.university.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Cathedra;
import ua.com.foxminded.university.model.Subject;
import ua.com.foxminded.university.model.Teacher;
import ua.com.foxminded.university.repository.SubjectRepository;
import ua.com.foxminded.university.repository.TeacherRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @InjectMocks
    private TeacherService teacherService;

    @Test
    public void shouldCreateTeacher() {
        Teacher teacher = new Teacher("ivan", "ivanov");
        Subject subject = new Subject();
        subject.setCathedra(new Cathedra());
        subject.getCathedra().setId(1);
        subject.setId(1);
        teacher.getSubjects().add(subject);

        when(subjectRepository.getOne(1)).thenReturn(subject);

        assertFalse(teacher.getCathedras().contains(subject.getCathedra()));

        teacherService.create(teacher);

        verify(subjectRepository).getOne(1);
        verify(teacherRepository).save(teacher);
        assertTrue(teacher.getCathedras().contains(subject.getCathedra()));
    }

    @Test
    public void shouldUpdateTeacher() {
        Teacher teacher = new Teacher("ivan", "ivanov");
        teacher.setVersion(0L);
        Subject subject = new Subject();
        subject.setCathedra(new Cathedra());
        subject.getCathedra().setId(1);
        subject.setId(1);
        teacher.getSubjects().add(subject);

        when(teacherRepository.getOne(teacher.getId())).thenReturn(teacher);
        when(subjectRepository.getOne(1)).thenReturn(subject);

        assertFalse(teacher.getCathedras().contains(subject.getCathedra()));

        teacherService.update(teacher);

        verify(subjectRepository).getOne(1);
        verify(teacherRepository).save(teacher);
        assertTrue(teacher.getCathedras().contains(subject.getCathedra()));
    }

    @Test
    public void shouldDeleteTeacher() {
        teacherService.delete(1);

        verify(teacherRepository).deleteById(1);
    }

    @Test
    public void shouldGetTeacher() {
        Teacher teacher = new Teacher("ivan", "ivanov");
        teacher.setId(1);

        when(teacherRepository.findById(1)).thenReturn(Optional.of(teacher));

        assertEquals(teacher, teacherService.get(1));
    }

    @Test
    public void shouldThrowExceptionWhenFindTeacherByNonExistentId() {
        when(teacherRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(WebNotFoundException.class, () -> teacherService.get(0));
    }

    @Test
    public void shouldGetAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        Teacher teacher1 = new Teacher("ivan", "ivanov");
        teacher1.setId(1);
        Teacher teacher2 = new Teacher("peter", "petrov");
        teacher2.setId(2);
        teachers.add(teacher1);
        teachers.add(teacher2);

        when(teacherRepository.findAll()).thenReturn(teachers);

        assertEquals(teachers, teacherService.getAll());
    }

}
