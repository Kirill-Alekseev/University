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
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.exceptions.UniversityExceptionHandler;
import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.service.ClassroomService;
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
public class ClassroomControllerTest {

    @Mock
    private ClassroomService classroomService;
    @InjectMocks
    private ClassroomController classroomController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(classroomController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new UniversityExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturnClassrooms() throws Exception {
        List<Classroom> expected = getExpectedClassrooms();

        when(classroomService.getAll(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")))))
                .thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/classrooms")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Classroom> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Classroom>>() {
        });
        assertEquals(new Slice<>(expected), actual);
    }

    @Test
    public void shouldReturnClassroom() throws Exception {
        Classroom expected = getExpectedClassroom();

        when(classroomService.get(1)).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/classrooms/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Classroom actual = JsonUtil.fromJson(contentAsString, Classroom.class);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenFindClassroomByNonExistentId() throws Exception {
        when(classroomService.get(0)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(get("/api/classrooms/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateClassroom() throws Exception {
        Classroom classroom = getExpectedClassroom();
        classroom.setId(null);

        mockMvc.perform(post("/api/classrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(classroom)))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .containsHeader("location");


        verify(classroomService).create(classroom);
    }

    @Test
    public void shouldNotCreateInvalidClassroom() throws Exception {
        Classroom classroom = new Classroom();
        classroom.setName("11");
        classroom.setCapacity(1);

        mockMvc.perform(post("/api/classrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(classroom)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors.name", is("Name is incorrect")))
                .andExpect(jsonPath("$.errors.capacity", is("Capacity should be from 5 to 100")));

        verify(classroomService, never()).create(any(Classroom.class));
    }

    @Test
    public void shouldUpdateClassroom() throws Exception {
        Classroom expected = getExpectedClassroom();
        expected.setId(null);

        when(classroomService.update(getExpectedClassroom())).thenReturn(getExpectedClassroom());

        String contentAsString = mockMvc.perform(put("/api/classrooms/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(expected)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Classroom actual = JsonUtil.fromJson(contentAsString, Classroom.class);

        assertEquals(getExpectedClassroom(), actual);
    }

    @Test
    public void shouldNotUpdateWithInvalidClassroom() throws Exception {
        Classroom classroom = new Classroom(1, "11", 1);
        classroom.setId(null);

        mockMvc.perform(put("/api/classrooms/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(classroom)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errors.name", is("Name is incorrect")))
                .andExpect(jsonPath("$.errors.capacity", is("Capacity should be from 5 to 100")));

        verify(classroomService, never()).update(any(Classroom.class));
    }

    @Test
    public void shouldDeleteClassroom() throws Exception {
        when(classroomService.get(1)).thenReturn(new Classroom());

        mockMvc.perform(delete("/api/classrooms/{id}", 1))
                .andExpect(status().is2xxSuccessful());

        verify(classroomService).delete(1);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenDeleteClassroomByNonExistentId() throws Exception {
        when(classroomService.get(1)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(delete("/api/classrooms/{id}", 1))
                .andExpect(status().is(404));

        verify(classroomService, never()).delete(1);
    }

    private List<Classroom> getExpectedClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();
        classrooms.add(new Classroom(1, "111", 11));
        classrooms.add(new Classroom(2, "222", 22));
        return classrooms;
    }

    private Classroom getExpectedClassroom() {
        return new Classroom(1, "111", 11);
    }
}
