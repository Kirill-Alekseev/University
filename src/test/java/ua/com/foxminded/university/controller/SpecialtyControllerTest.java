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
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.model.Specialty;
import ua.com.foxminded.university.service.FacultyService;
import ua.com.foxminded.university.service.SpecialtyService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SpecialtyControllerTest {

    @Mock
    private SpecialtyService specialtyService;
    @Mock
    private FacultyService facultyService;
    @InjectMocks
    private SpecialtyController specialtyController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(specialtyController).build();
    }

    @Test
    public void shouldReturnSpecialties() throws Exception {
        when(specialtyService.getAll()).thenReturn(getExpectedSpecialties());
        when(facultyService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/specialties"))
                .andExpect(view().name("specialties/all"))
                .andExpect(model().attribute("specialties", getExpectedSpecialties()));
    }

    @Test
    public void shouldReturnSpecialty() throws Exception {
        when(specialtyService.get(1)).thenReturn(getExpectedSpecialty());
        when(facultyService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/specialties/{id}", 1))
                .andExpect(view().name("specialties/one"))
                .andExpect(model().attribute("specialty", getExpectedSpecialty()));
    }

    @Test
    public void shouldDeleteSpecialty() throws Exception {
        mockMvc.perform(delete("/specialties/remove/{id}", 1))
                .andExpect(redirectedUrl("/specialties"));

        verify(specialtyService).delete(1);
    }

    @Test
    public void shouldCreateSpecialty() throws Exception {
        Specialty specialty = getExpectedSpecialty();
        specialty.setId(null);

        mockMvc.perform(post("/specialties/add")
                .param("name", specialty.getName())
                .param("faculty.id", Integer.toString(specialty.getFaculty().getId())))
                .andExpect(redirectedUrl("/specialties/" + specialty.getId()));

        verify(specialtyService).create(specialty);
    }

    @Test
    public void shouldNotCreateInvalidSpecialty() throws Exception {
        mockMvc.perform(post("/specialties/add")
                .param("name", "sp"))
                .andExpect(redirectedUrl("/specialties"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".specialty"));

        verify(specialtyService, never()).update(any(Specialty.class));
    }

    @Test
    public void shouldEditSpecialty() throws Exception {
        Specialty specialty = getExpectedSpecialty();

        mockMvc.perform(patch("/specialties/edit")
                .param("id", specialty.getId().toString())
                .param("name", specialty.getName())
                .param("faculty.id", specialty.getFaculty().getId().toString()))
                .andExpect(redirectedUrl("/specialties/" + specialty.getId()));

        verify(specialtyService).update(specialty);
    }

    @Test
    public void shouldNotEditWithInvalidSpecialty() throws Exception {
        mockMvc.perform(patch("/specialties/edit")
                .param("id", "1")
                .param("name", "sp"))
                .andExpect(redirectedUrl("/specialties/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".specialty"));

        verify(specialtyService, never()).update(any(Specialty.class));
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
