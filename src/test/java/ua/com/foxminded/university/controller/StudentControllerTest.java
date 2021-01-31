package ua.com.foxminded.university.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Student;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.service.StudentService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    @Mock
    private StudentService studentService;
    @Mock
    private GroupService groupService;
    @InjectMocks
    private StudentController studentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    @Test
    public void shouldReturnStudents() throws Exception {
        when(studentService.getAll()).thenReturn(getExpectedStudents());
        when(groupService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/students"))
                .andExpect(view().name("students/all"))
                .andExpect(model().attribute("students", getExpectedStudents()));
    }

    @Test
    public void shouldReturnStudent() throws Exception {
        when(studentService.get(1)).thenReturn(getExpectedStudent());
        when(groupService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/students/{id}", 1))
                .andExpect(view().name("students/one"))
                .andExpect(model().attribute("student", getExpectedStudent()));
    }

    @Test
    public void shouldDeleteStudent() throws Exception {
        mockMvc.perform(delete("/students/remove/{id}", 1))
                .andExpect(redirectedUrl("/students"));

        verify(studentService).delete(1);
    }

    @Test
    public void shouldCreateStudent() throws Exception {
        Student student = getExpectedStudent();
        student.setId(null);

        mockMvc.perform(post("/students/add")
                .param("firstName", student.getFirstName())
                .param("lastName", student.getLastName())
                .param("group.id", student.getGroup().getId().toString())
                .param("phoneNumber", student.getPhoneNumber())
                .param("email", student.getEmail())
                .param("birthDate", student.getBirthDate().toString()))
                .andExpect(redirectedUrl("/students/" + student.getId()));

        verify(studentService).create(student);
    }

    @Test
    public void shouldNotCreateInvalidStudent() throws Exception {
        mockMvc.perform(post("/students/add")
                .param("firstName", "firstName")
                .param("lastName", "lastName"))
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".student"));

        verify(studentService, never()).create(any(Student.class));
    }

    @Test
    public void shouldEditStudent() throws Exception {
        Student student = getExpectedStudent();

        mockMvc.perform(patch("/students/edit")
                .param("id", student.getId().toString())
                .param("firstName", student.getFirstName())
                .param("lastName", student.getLastName())
                .param("group.id", student.getGroup().getId().toString())
                .param("phoneNumber", student.getPhoneNumber())
                .param("email", student.getEmail())
                .param("birthDate", student.getBirthDate().toString()))
                .andExpect(redirectedUrl("/students/" + student.getId()));

        verify(studentService).update(student);
    }

    @Test
    public void shouldNotEditWithInvalidStudent() throws Exception {
        mockMvc.perform(patch("/students/edit")
                .param("id", "1")
                .param("firstName", "firstName")
                .param("lastName", "lastName"))
                .andExpect(redirectedUrl("/students/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".student"));

        verify(studentService, never()).update(any(Student.class));
    }

    private List<Student> getExpectedStudents() {
        List<Student> students = new ArrayList<>();
        students.add(new Student(1, "Firstname", "Lastname"));
        students.add(new Student(2, "Lastname", "Firstname"));
        return students;
    }

    private Student getExpectedStudent() {
        Student student = new Student(1, "Firstname", "Lastname");
        student.setBirthDate(LocalDate.of(2004, 1, 1));
        student.setPhoneNumber("+380999999999");
        student.setEmail("student@mail.com");
        student.setGroup(new Group());
        student.getGroup().setId(1);
        return student;
    }
}
