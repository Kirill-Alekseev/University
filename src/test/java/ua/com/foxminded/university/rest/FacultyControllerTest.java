package ua.com.foxminded.university.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.exceptions.UniversityExceptionHandler;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.service.FacultyService;
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
public class FacultyControllerTest {

    @Mock
    private FacultyService facultyService;
    @InjectMocks
    private FacultyController facultyController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(facultyController)
                .setControllerAdvice(new UniversityExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturnCorrectFaculties() throws Exception {
        List<Faculty> expected = getExpectedFaculties();

        when(facultyService.getAll()).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/faculties")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Faculty> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Faculty>>() {
        });

        assertEquals(new Slice<>(expected), actual);
    }

    @Test
    public void shouldReturnCorrectFaculty() throws Exception {
        Faculty expected = getExpectedFaculty();

        when(facultyService.get(1)).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/faculties/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Faculty actual = JsonUtil.fromJson(contentAsString, Faculty.class);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenFindFacultyByNonExistentId() throws Exception {
        when(facultyService.get(0)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(get("/api/faculties/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateFaculty() throws Exception {
        Faculty faculty = getExpectedFaculty();
        faculty.setId(null);

        mockMvc.perform(post("/api/faculties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(faculty)))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .containsHeader("location");

        verify(facultyService).create(faculty);
    }

    @Test
    public void shouldNotCreateInvalidFaculty() throws Exception {
        Faculty faculty = new Faculty(1);
        faculty.setName("fa");

        mockMvc.perform(post("/api/faculties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(faculty)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.name", is("Name is incorrect")));
    }


    @Test
    public void shouldUpdateFaculty() throws Exception {
        Faculty expected = getExpectedFaculty();
        expected.setId(null);

        when(facultyService.update(getExpectedFaculty())).thenReturn(getExpectedFaculty());

        String contentAsString = mockMvc.perform(put("/api/faculties/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(expected)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Faculty actual = JsonUtil.fromJson(contentAsString, Faculty.class);

        assertEquals(getExpectedFaculty(), actual);
    }

    @Test
    public void shouldNotUpdateWithInvalidFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("fa");

        mockMvc.perform(put("/api/faculties/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(faculty)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.name", is("Name is incorrect")));
    }

    @Test
    public void shouldDeleteFaculty() throws Exception {
        when(facultyService.get(1)).thenReturn(new Faculty());

        mockMvc.perform(delete("/api/faculties/{id}", 1))
                .andExpect(status().is2xxSuccessful());

        verify(facultyService).delete(1);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenDeleteFacultyByNonExistentId() throws Exception {
        when(facultyService.get(1)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(delete("/api/faculties/{id}", 1))
                .andExpect(status().is(404));

        verify(facultyService, never()).delete(1);
    }

    private List<Faculty> getExpectedFaculties() {
        List<Faculty> faculties = new ArrayList<>();
        Faculty faculty1 = new Faculty();
        faculty1.setId(1);
        faculty1.setName("Faculty of Art");
        Faculty faculty2 = new Faculty();
        faculty2.setId(2);
        faculty2.setName("Faculty of Informatics");
        faculties.add(faculty1);
        faculties.add(faculty2);
        return faculties;
    }

    private Faculty getExpectedFaculty() {
        Faculty faculty = new Faculty();
        faculty.setId(1);
        faculty.setName("Faculty of Art");
        return faculty;
    }
}
