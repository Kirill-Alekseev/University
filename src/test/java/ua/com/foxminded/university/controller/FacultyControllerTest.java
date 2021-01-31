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
import ua.com.foxminded.university.service.FacultyService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FacultyControllerTest {

    @Mock
    private FacultyService facultyService;
    @InjectMocks
    private FacultyController facultyController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(facultyController).build();
    }

    @Test
    public void shouldReturnFaculties() throws Exception {
        when(facultyService.getAll()).thenReturn(getExpectedFaculties());

        mockMvc.perform(get("/faculties"))
                .andExpect(view().name("faculties/all"))
                .andExpect(model().attribute("faculties", getExpectedFaculties()));
    }

    @Test
    public void shouldReturnFaculty() throws Exception {
        when(facultyService.get(1)).thenReturn(getExpectedFaculty());

        mockMvc.perform(get("/faculties/{id}", 1))
                .andExpect(view().name("faculties/one"))
                .andExpect(model().attribute("faculty", getExpectedFaculty()));
    }

    @Test
    public void shouldDeleteFaculty() throws Exception {
        mockMvc.perform(delete("/faculties/remove/{id}", 1))
                .andExpect(redirectedUrl("/faculties"));

        verify(facultyService).delete(1);
    }

    @Test
    public void shouldCreateFaculty() throws Exception {
        Faculty faculty = getExpectedFaculty();
        faculty.setId(null);

        mockMvc.perform(post("/faculties/add")
                .param("name", faculty.getName()))
                .andExpect(redirectedUrl("/faculties/" + faculty.getId()));

        verify(facultyService).create(faculty);
    }

    @Test
    public void shouldNotCreateInvalidFaculty() throws Exception {
        mockMvc.perform(post("/faculties/add")
                .param("name", "faculty"))
                .andExpect(redirectedUrl("/faculties"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".faculty"));

        verify(facultyService, never()).create(any(Faculty.class));
    }

    @Test
    public void shouldEditFaculty() throws Exception {
        Faculty faculty = getExpectedFaculty();

        mockMvc.perform(patch("/faculties/edit")
                .param("id", faculty.getId().toString())
                .param("name", faculty.getName()))
                .andExpect(redirectedUrl("/faculties/" + faculty.getId()));

        verify(facultyService).update(faculty);
    }

    @Test
    public void shouldNotEditWithInvalidFaculty() throws Exception {
        mockMvc.perform(patch("/faculties/edit")
                .param("id", "1")
                .param("name", "faculty"))
                .andExpect(redirectedUrl("/faculties/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".faculty"));
        ;

        verify(facultyService, never()).update(any(Faculty.class));
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
