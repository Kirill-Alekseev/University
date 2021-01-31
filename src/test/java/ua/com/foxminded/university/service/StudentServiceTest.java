package ua.com.foxminded.university.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.exceptions.DomainException;
import ua.com.foxminded.university.exceptions.GroupOverflowException;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Student;
import ua.com.foxminded.university.repository.GroupRepository;
import ua.com.foxminded.university.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private static StudentRepository studentRepository;
    @Mock
    private static GroupRepository groupRepository;
    @InjectMocks
    private static StudentService studentService;

    @BeforeAll
    public static void setStudentServiceProperties() {
        studentService = new StudentService(studentRepository, groupRepository);

        setField(studentService, "maxStudentsInGroup", 5);
    }

    @Test
    public void shouldThrowExceptionWhenCreateStudentWithOverflowGroup() {
        Student student = new Student("ivan", "ivanov");
        student.setId(1);
        Group group = new Group();
        student.setGroup(group);
        group.setId(1);

        whenRefreshGroup(student);
        when(studentRepository.countByGroupId(1)).thenReturn(6);

        assertThrows(GroupOverflowException.class, () -> studentService.create(student));

        verify(studentRepository, never()).save(student);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateStudentWithOverflowGroup() {
        Student student = new Student("ivan", "ivanov");
        student.setId(1);
        Group group = new Group();
        student.setGroup(group);
        group.setId(1);

        whenGetOneStudent(student);
        whenRefreshGroup(student);
        when(studentRepository.countByGroupId(1)).thenReturn(6);

        assertThrows(GroupOverflowException.class, () -> studentService.update(student));

        verify(studentRepository, never()).save(student);
    }

    @Test
    public void shouldCreateStudent() throws DomainException {
        Group group = new Group();
        group.setId(1);
        Student student = new Student("ivan", "ivanov");
        student.setGroup(group);

        whenRefreshGroup(student);
        when(studentRepository.countByGroupId(1)).thenReturn(5);

        studentService.create(student);

        verify(studentRepository).save(student);
    }

    @Test
    public void shouldUpdateStudent() throws DomainException {
        Group group = new Group();
        group.setId(1);
        Student student = new Student("ivan", "ivanov");
        student.setId(1);
        student.setGroup(group);

        whenGetOneStudent(student);
        whenRefreshGroup(student);
        when(studentRepository.countByGroupId(1)).thenReturn(5);

        studentService.update(student);

        verify(studentRepository).save(student);
    }

    @Test
    public void shouldDeleteStudent() {
        studentService.delete(1);

        verify(studentRepository).deleteById(1);
    }

    @Test
    public void shouldGetStudent() {
        Student student = new Student("ivan", "ivanov");
        student.setId(1);

        when(studentRepository.findById(1)).thenReturn(Optional.of(student));

        assertEquals(student, studentService.get(1));
    }

    @Test
    public void shouldThrowExceptionWhenFindStudentByNonExistentId() {
        when(studentRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(WebNotFoundException.class, () -> studentService.get(0));
    }

    @Test
    public void shouldGetAllStudents() {
        List<Student> students = new ArrayList<>();
        Student student1 = new Student("ivan", "ivanov");
        student1.setId(1);
        Student student2 = new Student("peter", "petrov");
        student2.setId(2);
        students.add(student1);
        students.add(student2);

        when(studentRepository.findAll()).thenReturn(students);

        assertEquals(students, studentService.getAll());
    }

    private void whenRefreshGroup(Student student) {
        Group group = new Group("group");
        group.setCourse(1);
        group.setId(1);
        group.setVersion(0L);

        when(groupRepository.getOne(student.getGroup().getId())).thenReturn(group);
    }

    private void whenGetOneStudent(Student student) {
        Student returnedStudent = new Student();
        returnedStudent.setId(1);
        returnedStudent.setVersion(0L);

        when(studentRepository.getOne(student.getId())).thenReturn(returnedStudent);
    }
}
