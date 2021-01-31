package ua.com.foxminded.university.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.exceptions.UniversityExceptionHandler;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Student;
import ua.com.foxminded.university.service.StudentService;
import ua.com.foxminded.university.util.JsonUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    @Mock
    private StudentService studentService;
    @InjectMocks
    private StudentController studentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(studentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new UniversityExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturnStudents() throws Exception {
        List<Student> expected = getExpectedStudents();

        when(studentService.getAll(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")))))
                .thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Student> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Student>>() {
        });

        assertEquals(new Slice<>(expected), actual);
    }

    @Test
    public void shouldReturnStudent() throws Exception {
        Student expected = getExpectedStudent();

        when(studentService.get(1)).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/students/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Student actual = JsonUtil.fromJson(contentAsString, Student.class);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenFindStudentByNonExistentId() throws Exception {
        when(studentService.get(0)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(get("/api/students/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateStudent() throws Exception {
        Student student = getExpectedStudent();
        student.setId(null);

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(student)))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .containsHeader("location");

        verify(studentService).create(student);
    }

    @Test
    public void shouldNotCreateInvalidStudent() throws Exception {
        Student student = new Student("first", "last");

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(student)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.firstName", is("Invalid person name")))
                .andExpect(jsonPath("$.errors.lastName", is("Invalid person name")))
                .andExpect(jsonPath("$.errors.group", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.email", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.birthDate", is("Invalid age")))
                .andExpect(jsonPath("$.errors.phoneNumber", is("Field is mandatory")));

        verify(studentService, never()).create(any(Student.class));
    }

    @Test
    public void shouldUpdateStudent() throws Exception {
        Student expected = getExpectedStudent();
        expected.setId(null);

        when(studentService.update(getExpectedStudent())).thenReturn(getExpectedStudent());

        String contentAsString = mockMvc.perform(put("/api/students/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(expected)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Student actual = JsonUtil.fromJson(contentAsString, Student.class);

        assertEquals(getExpectedStudent(), actual);
    }

    @Test
    public void shouldNotUpdateWithInvalidStudent() throws Exception {
        Student student = new Student("first", "last");

        mockMvc.perform(put("/api/students/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(student)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.firstName", is("Invalid person name")))
                .andExpect(jsonPath("$.errors.lastName", is("Invalid person name")))
                .andExpect(jsonPath("$.errors.group", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.email", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.birthDate", is("Invalid age")))
                .andExpect(jsonPath("$.errors.phoneNumber", is("Field is mandatory")));

        verify(studentService, never()).update(any(Student.class));
    }

    @Test
    public void shouldDeleteStudent() throws Exception {
        when(studentService.get(1)).thenReturn(new Student());

        mockMvc.perform(delete("/api/students/{id}", 1))
                .andExpect(status().is2xxSuccessful());

        verify(studentService).delete(1);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenDeleteStudentByNonExistentId() throws Exception {
        when(studentService.get(1)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(delete("/api/students/{id}", 1))
                .andExpect(status().is(404));

        verify(studentService, never()).delete(1);
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
