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
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Specialty;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.service.SpecialtyService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GroupControllerTest {

    @Mock
    private GroupService groupService;
    @Mock
    private SpecialtyService specialtyService;
    @InjectMocks
    private GroupController groupController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }

    @Test
    public void shouldReturnGroups() throws Exception {
        when(groupService.getAll()).thenReturn(getExpectedGroups());
        when(specialtyService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/groups"))
                .andExpect(view().name("groups/all"))
                .andExpect(model().attribute("groups", getExpectedGroups()));
    }

    @Test
    public void shouldReturnGroup() throws Exception {
        when(groupService.get(1)).thenReturn(getExpectedGroup());
        when(specialtyService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/groups/{id}", 1))
                .andExpect(view().name("groups/one"))
                .andExpect(model().attribute("group", getExpectedGroup()));
    }

    @Test
    public void shouldDeleteGroup() throws Exception {
        mockMvc.perform(delete("/groups/remove/{id}", 1))
                .andExpect(redirectedUrl("/groups"));

        verify(groupService).delete(1);
    }

    @Test
    public void shouldCreateGroup() throws Exception {
        Group group = getExpectedGroup();
        group.setId(null);

        mockMvc.perform(post("/groups/add")
                .param("name", group.getName())
                .param("course", Integer.toString(group.getCourse()))
                .param("specialty.id", group.getSpecialty().getId().toString()))
                .andExpect(redirectedUrl("/groups/" + group.getId()));

        verify(groupService).create(group);
    }

    @Test
    public void shouldNotCreateInvalidGroup() throws Exception {
        mockMvc.perform(post("/groups/add")
                .param("name", "a1")
                .param("course", "0"))
                .andExpect(redirectedUrl("/groups"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".group"));

        verify(groupService, never()).create(any(Group.class));
    }

    @Test
    public void shouldEditGroup() throws Exception {
        Group group = getExpectedGroup();

        mockMvc.perform(patch("/groups/edit")
                .param("id", group.getId().toString())
                .param("name", group.getName())
                .param("course", Integer.toString(group.getCourse()))
                .param("specialty.id", group.getSpecialty().getId().toString()))
                .andExpect(redirectedUrl("/groups/" + group.getId()));

        verify(groupService).update(group);
    }

    @Test
    public void shouldNotEditWithInvalidGroup() throws Exception {
        mockMvc.perform(patch("/groups/edit")
                .param("id", "1")
                .param("name", "a1")
                .param("course", "0"))
                .andExpect(redirectedUrl("/groups/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".group"));

        verify(groupService, never()).update(any(Group.class));
    }

    private List<Group> getExpectedGroups() {
        List<Group> groups = new ArrayList<>();
        Specialty specialty = new Specialty("specialty");
        specialty.setId(1);
        groups.add(new Group(1, "QQ-11", 1));
        groups.get(0).setSpecialty(specialty);
        groups.add(new Group(2, "WW-22", 2));
        groups.get(1).setSpecialty(specialty);
        return groups;
    }

    private Group getExpectedGroup() {
        Specialty specialty = new Specialty("specialty");
        specialty.setId(1);
        Group group = new Group(1, "QQ-11", 1);
        group.setSpecialty(specialty);
        return group;
    }

}
