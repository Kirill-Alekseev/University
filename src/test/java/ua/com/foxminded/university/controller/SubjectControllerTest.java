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
import ua.com.foxminded.university.model.Subject;
import ua.com.foxminded.university.service.CathedraService;
import ua.com.foxminded.university.service.SubjectService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SubjectControllerTest {

    @Mock
    private SubjectService subjectService;
    @Mock
    private CathedraService cathedraService;
    @InjectMocks
    private SubjectController subjectController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subjectController).build();
    }

    @Test
    public void shouldReturnSubjects() throws Exception {
        when(subjectService.getAll()).thenReturn(getExpectedSubjects());
        when(cathedraService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/subjects"))
                .andExpect(view().name("subjects/all"))
                .andExpect(model().attribute("subjects", getExpectedSubjects()));
    }

    @Test
    public void shouldReturnSubject() throws Exception {
        when(subjectService.get(1)).thenReturn(getExpectedSubject());
        when(cathedraService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/subjects/{id}", 1))
                .andExpect(view().name("subjects/one"))
                .andExpect(model().attribute("subject", getExpectedSubject()));
    }

    @Test
    public void shouldDeleteSubject() throws Exception {
        mockMvc.perform(delete("/subjects/remove/{id}", 1))
                .andExpect(redirectedUrl("/subjects"));

        verify(subjectService).delete(1);
    }

    @Test
    public void shouldCreateSubject() throws Exception {
        Subject subject = getExpectedSubject();
        subject.setId(null);

        mockMvc.perform(post("/subjects/add")
                .param("name", subject.getName())
                .param("cathedra.id", subject.getCathedra().getId().toString()))
                .andExpect(redirectedUrl("/subjects/" + subject.getId()));

        verify(subjectService).create(subject);
    }

    @Test
    public void shouldNotCreateInvalidSubject() throws Exception {
        mockMvc.perform(post("/subjects/add")
                .param("name", "subject"))
                .andExpect(redirectedUrl("/subjects"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".subject"));

        verify(subjectService, never()).create(any(Subject.class));
    }

    @Test
    public void shouldEditSubject() throws Exception {
        Subject subject = getExpectedSubject();

        mockMvc.perform(patch("/subjects/edit")
                .param("id", subject.getId().toString())
                .param("name", subject.getName())
                .param("cathedra.id", subject.getCathedra().getId().toString()))
                .andExpect(redirectedUrl("/subjects/" + subject.getId()));

        verify(subjectService).update(subject);
    }

    @Test
    public void shouldNotEditWithInvalidSubject() throws Exception {
        mockMvc.perform(patch("/subjects/edit")
                .param("id", "1")
                .param("name", "subject"))
                .andExpect(redirectedUrl("/subjects/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".subject"));

        verify(subjectService, never()).update(any(Subject.class));
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
