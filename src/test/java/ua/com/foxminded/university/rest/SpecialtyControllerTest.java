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
import ua.com.foxminded.university.exceptions.UniversityExceptionHandler;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.model.Specialty;
import ua.com.foxminded.university.service.SpecialtyService;
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
public class SpecialtyControllerTest {

    @Mock
    private SpecialtyService specialtyService;
    @InjectMocks
    private SpecialtyController specialtyController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(specialtyController)
                .setControllerAdvice(new UniversityExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturnCorrectSpecialties() throws Exception {
        List<Specialty> expected = getExpectedSpecialties();

        when(specialtyService.getAll()).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Specialty> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Specialty>>() {
        });

        assertEquals(new Slice<>(expected), actual);
    }

    @Test
    public void shouldReturnCorrectSpecialty() throws Exception {
        Specialty expected = getExpectedSpecialty();

        when(specialtyService.get(1)).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/specialties/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Specialty actual = JsonUtil.fromJson(contentAsString, Specialty.class);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenFindSpecialtyByNonExistentId() throws Exception {
        when(specialtyService.get(0)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(get("/api/specialties/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateSpecialty() throws Exception {
        Specialty specialty = getExpectedSpecialty();
        specialty.setId(null);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(specialty)))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .containsHeader("location");

        verify(specialtyService).create(specialty);
    }

    @Test
    public void shouldNotCreateInvalidSpecialty() throws Exception {
        Specialty specialty = new Specialty("sp");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(specialty)))
                .andExpect(jsonPath("$.errors.name", is("Name is incorrect")))
                .andExpect(jsonPath("$.errors.faculty", is("Field is mandatory")));

        verify(specialtyService, never()).create(any(Specialty.class));
    }

    @Test
    public void shouldUpdateSpecialty() throws Exception {
        Specialty expected = getExpectedSpecialty();
        expected.setId(null);

        when(specialtyService.update(getExpectedSpecialty())).thenReturn(getExpectedSpecialty());

        String contentAsString = mockMvc.perform(put("/api/specialties/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(expected)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Specialty actual = JsonUtil.fromJson(contentAsString, Specialty.class);

        assertEquals(getExpectedSpecialty(), actual);
    }

    @Test
    public void shouldNotUpdateWithInvalidSpecialty() throws Exception {
        Specialty specialty = new Specialty("sp");

        mockMvc.perform(put("/api/specialties/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(specialty)))
                .andExpect(jsonPath("$.errors.name", is("Name is incorrect")))
                .andExpect(jsonPath("$.errors.faculty", is("Field is mandatory")));

        verify(specialtyService, never()).update(any(Specialty.class));
    }

    @Test
    public void shouldDeleteSpecialty() throws Exception {
        when(specialtyService.get(1)).thenReturn(new Specialty());

        mockMvc.perform(delete("/api/specialties/{id}", 1))
                .andExpect(status().is2xxSuccessful());

        verify(specialtyService).delete(1);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenDeleteSpecialtyByNonExistentId() throws Exception {
        when(specialtyService.get(1)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(delete("/api/specialties/{id}", 1))
                .andExpect(status().is(404));

        verify(specialtyService, never()).delete(1);
    }

    private List<Specialty> getExpectedSpecialties() {
        List<Specialty> specialties = new ArrayList<>();
        Faculty faculty = new Faculty();
        faculty.setId(1);
        Specialty specialty1 = new Specialty("Specialty");
        specialty1.setId(1);
        specialty1.setFaculty(faculty);
        Specialty specialty2 = new Specialty("Specialty");
        specialty2.setId(2);
        specialty2.setFaculty(faculty);
        specialties.add(specialty1);
        specialties.add(specialty2);
        return specialties;
    }

    private Specialty getExpectedSpecialty() {
        Faculty faculty = new Faculty();
        faculty.setId(1);
        Specialty specialty = new Specialty("Specialty");
        specialty.setId(1);
        specialty.setFaculty(faculty);
        return specialty;
    }
}
