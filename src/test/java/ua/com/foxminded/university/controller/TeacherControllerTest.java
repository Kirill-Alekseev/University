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
import ua.com.foxminded.university.model.Teacher;
import ua.com.foxminded.university.service.SubjectService;
import ua.com.foxminded.university.service.TeacherService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;
    @Mock
    private SubjectService subjectService;
    @InjectMocks
    private TeacherController teacherController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(teacherController).build();
    }

    @Test
    public void shouldReturnTeachers() throws Exception {
        when(teacherService.getAll()).thenReturn(getExpectedTeachers());

        mockMvc.perform(get("/teachers"))
                .andExpect(view().name("teachers/all"))
                .andExpect(model().attribute("teachers", getExpectedTeachers()));
    }

    @Test
    public void shouldReturnTeacher() throws Exception {
        when(teacherService.get(1)).thenReturn(getExpectedTeacher());
        when(subjectService.getAll()).thenReturn(getExpectedSubjects());

        mockMvc.perform(get("/teachers/{id}", 1))
                .andExpect(view().name("teachers/one"))
                .andExpect(model().attribute("teacher", getExpectedTeacher()))
                .andExpect(model().attribute("subjects", getExpectedSubjects()));
    }

    @Test
    public void shouldDeleteTeacher() throws Exception {
        mockMvc.perform(delete("/teachers/remove/{id}", 1))
                .andExpect(redirectedUrl("/teachers"));

        verify(teacherService).delete(1);
    }

    @Test
    public void shouldCreateTeacher() throws Exception {
        Teacher teacher = getExpectedTeacher();
        teacher.setId(null);

        mockMvc.perform(post("/teachers/add")
                .param("firstName", teacher.getFirstName())
                .param("lastName", teacher.getLastName()))
                .andExpect(redirectedUrl("/teachers/" + teacher.getId()));

        verify(teacherService).create(teacher);
    }

    @Test
    public void shouldNotCreateInvalidTeacher() throws Exception {
        mockMvc.perform(post("/teachers/add")
                .param("firstName", "firstName")
                .param("lastName", "lastName"))
                .andExpect(redirectedUrl("/teachers"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".teacher"));

        verify(teacherService, never()).create(any(Teacher.class));
    }

    @Test
    public void shouldEditTeacher() throws Exception {
        Teacher teacher = getExpectedTeacher();

        mockMvc.perform(patch("/teachers/edit")
                .param("id", teacher.getId().toString())
                .param("firstName", teacher.getFirstName())
                .param("lastName", teacher.getLastName())
                .param("subjects", "1"))
                .andExpect(redirectedUrl("/teachers/" + teacher.getId()));

        verify(teacherService).update(teacher);
    }

    @Test
    public void shouldNotEditWithInvalidTeacher() throws Exception {
        mockMvc.perform(patch("/teachers/edit")
                .param("id", "1")
                .param("firstName", "firstName")
                .param("lastName", "lastName"))
                .andExpect(redirectedUrl("/teachers/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".teacher"));

        verify(teacherService, never()).update(any(Teacher.class));
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

    private List<Subject> getExpectedSubjects() {
        List<Subject> subjects = new ArrayList<>();
        Cathedra cathedra = new Cathedra();
        cathedra.setId(1);
        subjects.add(new Subject("subject1"));
        subjects.add(new Subject("subject1"));
        subjects.get(0).setId(1);
        subjects.get(1).setId(2);
        subjects.get(0).setCathedra(cathedra);
        subjects.get(1).setCathedra(cathedra);
        return subjects;
    }
}
