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
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Specialty;
import ua.com.foxminded.university.service.GroupService;
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
public class GroupControllerTest {

    @Mock
    private GroupService groupService;
    @InjectMocks
    private GroupController groupController;


    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(groupController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new UniversityExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturnGroups() throws Exception {
        List<Group> expected = getExpectedGroups();

        when(groupService.getAll(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")))))
                .thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/groups")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Group> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Group>>() {
        });

        assertEquals(new Slice<>(expected), actual);
    }

    @Test
    public void shouldReturnGroup() throws Exception {
        Group expected = getExpectedGroup();

        when(groupService.get(1)).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/groups/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Group actual = JsonUtil.fromJson(contentAsString, Group.class);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenFindGroupByNonExistentId() throws Exception {
        when(groupService.get(0)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(get("/api/groups/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateGroup() throws Exception {
        Group group = getExpectedGroup();
        group.setId(null);

        mockMvc.perform(post("/api/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(group)))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .containsHeader("location");

        verify(groupService).create(group);
    }

    @Test
    public void shouldNotCreateInvalidGroup() throws Exception {
        Group group = new Group();
        group.setName("a-1");
        group.setCourse(0);

        mockMvc.perform(post("/api/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(group)))
                .andExpect(jsonPath("$.errors.name",
                        is("Group name should be 2 capital letters, hyphen and 2 digits")))
                .andExpect(jsonPath("$.errors.course", is("Course should be from 1 to 6")))
                .andExpect(jsonPath("$.errors.specialty", is("Field is mandatory")));
    }

    @Test
    public void shouldUpdateGroup() throws Exception {
        Group expected = getExpectedGroup();
        expected.setId(null);

        when(groupService.update(getExpectedGroup())).thenReturn(getExpectedGroup());

        String contentAsString = mockMvc.perform(put("/api/groups/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(expected)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Group actual = JsonUtil.fromJson(contentAsString, Group.class);

        assertEquals(getExpectedGroup(), actual);
    }

    @Test
    public void shouldNotUpdateWithInvalidGroup() throws Exception {
        Group group = new Group(null, "a-1", 0);

        mockMvc.perform(put("/api/groups/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(group)))
                .andExpect(jsonPath("$.errors.name",
                        is("Group name should be 2 capital letters, hyphen and 2 digits")))
                .andExpect(jsonPath("$.errors.course", is("Course should be from 1 to 6")))
                .andExpect(jsonPath("$.errors.specialty", is("Field is mandatory")));
    }

    @Test
    public void shouldDeleteGroup() throws Exception {
        when(groupService.get(1)).thenReturn(new Group());

        mockMvc.perform(delete("/api/groups/{id}", 1))
                .andExpect(status().is2xxSuccessful());

        verify(groupService).delete(1);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenDeleteGroupByNonExistentId() throws Exception {
        when(groupService.get(1)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(delete("/api/groups/{id}", 1))
                .andExpect(status().is(404));

        verify(groupService, never()).delete(1);
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
