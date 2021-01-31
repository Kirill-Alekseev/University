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
import ua.com.foxminded.university.model.Cathedra;
import ua.com.foxminded.university.model.Subject;
import ua.com.foxminded.university.service.SubjectService;
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
public class SubjectControllerTest {

    @Mock
    private SubjectService subjectService;
    @InjectMocks
    private SubjectController subjectController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(subjectController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new UniversityExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturnCorrectSubjects() throws Exception {
        List<Subject> expected = getExpectedSubjects();

        when(subjectService.getAll(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")))))
                .thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/subjects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Subject> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Subject>>() {
        });

        assertEquals(new Slice<>(expected), actual);
    }

    @Test
    public void shouldReturnCorrectSubject() throws Exception {
        Subject expected = getExpectedSubject();

        when(subjectService.get(1)).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/subjects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Subject actual = JsonUtil.fromJson(contentAsString, Subject.class);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenFindSubjectByNonExistentId() throws Exception {
        when(subjectService.get(0)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(get("/api/subjects/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateSubject() throws Exception {
        Subject subject = getExpectedSubject();
        subject.setId(null);

        mockMvc.perform(post("/api/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(subject)))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .containsHeader("location");

        verify(subjectService).create(subject);
    }

    @Test
    public void shouldNotCreateInvalidSubject() throws Exception {
        Subject subject = new Subject("su");

        mockMvc.perform(post("/api/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(subject)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.name", is("Name is incorrect")))
                .andExpect(jsonPath("$.errors.cathedra", is("Field is mandatory")));

        verify(subjectService, never()).create(any(Subject.class));
    }

    @Test
    public void shouldUpdateSubject() throws Exception {
        Subject expected = getExpectedSubject();
        expected.setId(null);

        when(subjectService.update(getExpectedSubject())).thenReturn(getExpectedSubject());

        String contentAsString = mockMvc.perform(put("/api/subjects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(expected)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Subject actual = JsonUtil.fromJson(contentAsString, Subject.class);

        assertEquals(getExpectedSubject(), actual);
    }

    @Test
    public void shouldNotUpdateWithInvalidSubject() throws Exception {
        Subject subject = new Subject("su");

        mockMvc.perform(put("/api/subjects/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(subject)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.name", is("Name is incorrect")))
                .andExpect(jsonPath("$.errors.cathedra", is("Field is mandatory")));

        verify(subjectService, never()).update(any(Subject.class));
    }

    @Test
    public void shouldDeleteSubject() throws Exception {
        when(subjectService.get(1)).thenReturn(new Subject());

        mockMvc.perform(delete("/api/subjects/{id}", 1))
                .andExpect(status().is2xxSuccessful());

        verify(subjectService).delete(1);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenDeleteSubjectByNonExistentId() throws Exception {
        when(subjectService.get(1)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(delete("/api/subjects/{id}", 1))
                .andExpect(status().is(404));

        verify(subjectService, never()).delete(1);
    }

    private List<Subject> getExpectedSubjects() {
        List<Subject> subjects = new ArrayList<>();
        Cathedra cathedra = new Cathedra();
        cathedra.setId(1);
        subjects.add(new Subject("Subject"));
        subjects.add(new Subject("Subject"));
        subjects.get(0).setId(1);
        subjects.get(1).setId(2);
        subjects.get(0).setCathedra(cathedra);
        subjects.get(1).setCathedra(cathedra);
        return subjects;
    }

    private Subject getExpectedSubject() {
        Cathedra cathedra = new Cathedra();
        cathedra.setId(1);
        Subject subject = new Subject("Subject");
        subject.setId(1);
        subject.setCathedra(cathedra);
        return subject;
    }
}
