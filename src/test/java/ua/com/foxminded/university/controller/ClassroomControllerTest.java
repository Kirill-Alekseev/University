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
import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.service.ClassroomService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ClassroomControllerTest {

    @Mock
    private ClassroomService classroomService;
    @InjectMocks
    private ClassroomController classroomController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(classroomController).build();
    }

    @Test
    public void shouldReturnClassrooms() throws Exception {
        when(classroomService.getAll()).thenReturn(getExpectedClassrooms());

        mockMvc.perform(get("/classrooms"))
                .andExpect(view().name("classrooms/all"))
                .andExpect(model().attribute("classrooms", getExpectedClassrooms()));
    }

    @Test
    public void shouldReturnClassroom() throws Exception {
        when(classroomService.get(1)).thenReturn(getExpectedClassroom());

        mockMvc.perform(get("/classrooms/{id}", 1))
                .andExpect(view().name("classrooms/one"))
                .andExpect(model().attribute("classroom", getExpectedClassroom()));
    }

    @Test
    public void shouldDeleteClassroom() throws Exception {
        mockMvc.perform(delete("/classrooms/remove/{id}", 1))
                .andExpect(redirectedUrl("/classrooms"));

        verify(classroomService).delete(1);
    }

    @Test
    public void shouldCreateClassroom() throws Exception {
        Classroom classroom = getExpectedClassroom();
        classroom.setId(null);

        mockMvc.perform(post("/classrooms/add")
                .param("name", classroom.getName())
                .param("capacity", Integer.toString(classroom.getCapacity())))
                .andExpect(redirectedUrl("/classrooms/" + classroom.getId()));

        verify(classroomService).create(classroom);
    }

    @Test
    public void shouldNotCreateInvalidClassroom() throws Exception {
        mockMvc.perform(post("/classrooms/add")
                .param("name", "11")
                .param("capacity", "4"))
                .andExpect(redirectedUrl("/classrooms"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".classroom"));


        verify(classroomService, never()).create(any(Classroom.class));
    }

    @Test
    public void shouldEditClassroom() throws Exception {
        Classroom classroom = getExpectedClassroom();

        mockMvc.perform(patch("/classrooms/edit")
                .param("id", classroom.getId().toString())
                .param("name", classroom.getName())
                .param("capacity", Integer.toString(classroom.getCapacity())))
                .andExpect(redirectedUrl("/classrooms/" + classroom.getId()));

        verify(classroomService).update(classroom);
    }

    @Test
    public void shouldNotEditWithInvalidClassroom() throws Exception {
        mockMvc.perform(patch("/classrooms/edit")
                .param("id", "1")
                .param("name", "11")
                .param("capacity", "4"))
                .andExpect(redirectedUrl("/classrooms/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".classroom"));

        verify(classroomService, never()).update(any(Classroom.class));
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
