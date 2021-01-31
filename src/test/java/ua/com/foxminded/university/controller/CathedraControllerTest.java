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
import ua.com.foxminded.university.model.Cathedra;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.service.CathedraService;
import ua.com.foxminded.university.service.FacultyService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CathedraControllerTest {

    @Mock
    private CathedraService cathedraService;
    @Mock
    private FacultyService facultyService;
    @InjectMocks
    private CathedraController cathedraController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cathedraController).build();
    }

    @Test
    public void shouldReturnCathedras() throws Exception {
        when(cathedraService.getAll()).thenReturn(getExpectedCathedras());
        when(facultyService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/cathedras"))
                .andExpect(view().name("cathedras/all"))
                .andExpect(model().attribute("cathedras", getExpectedCathedras()));
    }

    @Test
    public void shouldReturnCathedra() throws Exception {
        when(cathedraService.get(1)).thenReturn(getExpectedCathedra());
        when(facultyService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/cathedras/{id}", 1))
                .andExpect(view().name("cathedras/one"))
                .andExpect(model().attribute("cathedra", getExpectedCathedra()));
    }

    @Test
    public void shouldDeleteCathedra() throws Exception {
        mockMvc.perform(delete("/cathedras/remove/{id}", 1))
                .andExpect(redirectedUrl("/cathedras"));

        verify(cathedraService).delete(1);
    }

    @Test
    public void shouldCreateCathedra() throws Exception {
        Cathedra cathedra = getExpectedCathedra();
        cathedra.setId(null);

        mockMvc.perform(post("/cathedras/add")
                .param("name", cathedra.getName())
                .param("faculty.id", cathedra.getFaculty().getId().toString()))
                .andExpect(redirectedUrl("/cathedras/" + cathedra.getId()));

        verify(cathedraService).create(cathedra);
    }

    @Test
    public void shouldNotCreateInvalidCathedra() throws Exception {
        mockMvc.perform(post("/cathedras/add")
                .param("name", "1cathedra"))
                .andExpect(redirectedUrl("/cathedras"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".cathedra"));

        verify(cathedraService, never()).create(any(Cathedra.class));
    }

    @Test
    public void shouldEditCathedra() throws Exception {
        Cathedra cathedra = getExpectedCathedra();

        mockMvc.perform(patch("/cathedras/edit")
                .param("id", cathedra.getId().toString())
                .param("name", cathedra.getName())
                .param("faculty.id", cathedra.getFaculty().getId().toString()))
                .andExpect(redirectedUrl("/cathedras/" + cathedra.getId()));

        verify(cathedraService).update(cathedra);
    }

    @Test
    public void shouldNotEditWithInvalidCathedra() throws Exception {
        mockMvc.perform(patch("/cathedras/edit")
                .param("id", "1")
                .param("name", "cathedra"))
                .andExpect(redirectedUrl("/cathedras/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".cathedra"));

        verify(cathedraService, never()).update(any(Cathedra.class));
    }

    private List<Cathedra> getExpectedCathedras() {
        List<Cathedra> cathedras = new ArrayList<>();
        Faculty faculty = new Faculty(1);
        faculty.setName("faculty");
        faculty.setVersion(0L);
        cathedras.add(new Cathedra(1, "Cathedra of Art", faculty));
        cathedras.add(new Cathedra(2, "Cathedra of Math", faculty));
        cathedras.get(1).setFaculty(faculty);
        return cathedras;
    }

    private Cathedra getExpectedCathedra() {
        Faculty faculty = new Faculty();
        faculty.setId(1);
        faculty.setName("faculty");
        return new Cathedra(1, "Cathedra of Art", faculty);
    }
}
