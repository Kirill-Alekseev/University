package ua.com.foxminded.university.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.exceptions.UniversityExceptionHandler;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Cathedra;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.service.CathedraService;
import ua.com.foxminded.university.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CathedraControllerTest {

    @Mock
    private CathedraService cathedraService;
    @InjectMocks
    private CathedraController cathedraController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(cathedraController)
                .setControllerAdvice(new UniversityExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturnCathedras() throws Exception {
        List<Cathedra> expected = getExpectedCathedras();

        when(cathedraService.getAll()).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/cathedras")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Cathedra> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Cathedra>>() {
        });

        assertEquals(new Slice<>(expected), actual);
    }

    @Test
    public void shouldReturnCathedra() throws Exception {
        Cathedra expectedCathedra = getExpectedCathedra();

        when(cathedraService.get(1)).thenReturn(expectedCathedra);

        String contentAsString = mockMvc.perform(get("/api/cathedras/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Cathedra cathedra = JsonUtil.fromJson(contentAsString, Cathedra.class);

        assertEquals(expectedCathedra, cathedra);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenFindCathedraByNonExistentId() throws Exception {
        when(cathedraService.get(0)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(get("/api/cathedras/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateCathedra() throws Exception {
        Cathedra cathedra = getExpectedCathedra();
        cathedra.setId(null);

        doAnswer((Answer<Void>) invocationOnMock -> {
            Cathedra argument = invocationOnMock.getArgument(0, Cathedra.class);
            argument.setId(1);
            return null;
        }).when(cathedraService).create(cathedra);

        mockMvc.perform(post("/api/cathedras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(cathedra)))
                .andExpect(header().string("location", "http://localhost/api/cathedras/1"));

        verify(cathedraService).create(getExpectedCathedra());
    }


    @Test
    public void shouldNotCreateInvalidCathedra() throws Exception {
        Cathedra cathedra = new Cathedra("ca");

        mockMvc.perform(post("/api/cathedras")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(cathedra)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.name", is("Name is incorrect")))
                .andExpect(jsonPath("$.errors.faculty", is("Field is mandatory")));

        verify(cathedraService, never()).create(any(Cathedra.class));
    }

    @Test
    public void shouldUpdateCathedra() throws Exception {
        Cathedra expected = getExpectedCathedra();
        expected.setId(null);

        when(cathedraService.update(getExpectedCathedra())).thenReturn(getExpectedCathedra());

        String contentAsString = mockMvc.perform(put("/api/cathedras/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(expected)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Cathedra actual = JsonUtil.fromJson(contentAsString, Cathedra.class);

        assertEquals(getExpectedCathedra(), actual);
    }

    @Test
    public void shouldNotUpdateWithInvalidCathedra() throws Exception {
        Cathedra cathedra = new Cathedra("ca");

        mockMvc.perform(put("/api/cathedras/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(cathedra)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.name", is("Name is incorrect")))
                .andExpect(jsonPath("$.errors.faculty", is("Field is mandatory")));

        verify(cathedraService, never()).update(any(Cathedra.class));
    }

    @Test
    public void shouldDeleteCathedra() throws Exception {
        when(cathedraService.get(1)).thenReturn(new Cathedra());

        mockMvc.perform(delete("/api/cathedras/{id}", 1))
                .andExpect(status().is2xxSuccessful());

        verify(cathedraService).delete(1);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenDeleteCathedraByNonExistentId() throws Exception {
        when(cathedraService.get(1)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(delete("/api/cathedras/{id}", 1))
                .andExpect(status().is(404));

        verify(cathedraService, never()).delete(1);
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
