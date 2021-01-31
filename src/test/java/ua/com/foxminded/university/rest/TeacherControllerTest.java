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
import ua.com.foxminded.university.model.Teacher;
import ua.com.foxminded.university.service.TeacherService;
import ua.com.foxminded.university.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;
    @InjectMocks
    private TeacherController teacherController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(teacherController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new UniversityExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturnTeachers() throws Exception {
        List<Teacher> expected = getExpectedTeachers();

        when(teacherService.getAll(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")))))
                .thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/teachers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Teacher> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Teacher>>() {
        });

        assertEquals(new Slice<>(expected), actual);
    }

    @Test
    public void shouldReturnTeacher() throws Exception {
        Teacher expected = getExpectedTeacher();

        when(teacherService.get(1)).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/teachers/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Teacher actual = JsonUtil.fromJson(contentAsString, Teacher.class);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenFindTeacherByNonExistentId() throws Exception {
        when(teacherService.get(0)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(get("/api/teachers/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateTeacher() throws Exception {
        Teacher teacher = getExpectedTeacher();
        teacher.setId(null);

        mockMvc.perform(post("/api/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(teacher)))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .containsHeader("location");

        verify(teacherService).create(teacher);
    }

    @Test
    public void shouldNotCreateInvalidTeacher() throws Exception {
        Teacher teacher = new Teacher("first", "last");

        mockMvc.perform(post("/api/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(teacher)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.firstName", is("Invalid person name")))
                .andExpect(jsonPath("$.errors.lastName", is("Invalid person name")));

        verify(teacherService, never()).create(any(Teacher.class));
    }

    @Test
    public void shouldUpdateTeacher() throws Exception {
        Teacher expected = getExpectedTeacher();
        expected.setId(null);

        when(teacherService.update(getExpectedTeacher())).thenReturn(getExpectedTeacher());

        String contentAsString = mockMvc.perform(put("/api/teachers/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(expected)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Teacher actual = JsonUtil.fromJson(contentAsString, Teacher.class);

        assertEquals(getExpectedTeacher(), actual);
    }

    @Test
    public void shouldNotUpdateWithInvalidTeacher() throws Exception {
        Teacher teacher = new Teacher(null, "first", "last");

        mockMvc.perform(put("/api/teachers/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(teacher)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.firstName", is("Invalid person name")))
                .andExpect(jsonPath("$.errors.lastName", is("Invalid person name")));

        verify(teacherService, never()).update(any(Teacher.class));
    }

    @Test
    public void shouldDeleteTeacher() throws Exception {
        when(teacherService.get(1)).thenReturn(new Teacher());

        mockMvc.perform(delete("/api/teachers/{id}", 1))
                .andExpect(status().is2xxSuccessful());

        verify(teacherService).delete(1);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenDeleteTeacherByNonExistentId() throws Exception {
        when(teacherService.get(1)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(delete("/api/teachers/{id}", 1))
                .andExpect(status().is(404));

        verify(teacherService, never()).delete(1);
    }

    private List<Teacher> getExpectedTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        Teacher teacher1 = new Teacher("Firstname", "Lastname");
        teacher1.setId(1);
        Teacher teacher2 = new Teacher("Lastname", "Firstname");
        teacher1.setId(2);
        teachers.add(teacher1);
        teachers.add(teacher2);
        return teachers;
    }

    private Teacher getExpectedTeacher() {
        Teacher teacher = new Teacher("Firstname", "Lastname");
        teacher.setId(1);
        return teacher;
    }
}
