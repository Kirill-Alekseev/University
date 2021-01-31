package ua.com.foxminded.university.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Specialty;
import ua.com.foxminded.university.repository.GroupRepository;
import ua.com.foxminded.university.repository.SpecialtyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;
    @Mock
    private GroupRepository groupRepository;
    @InjectMocks
    private GroupService groupService;

    @Test
    public void shouldCreateGroup() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        Group group = new Group("group");
        group.setCourse(1);
        group.setSpecialty(specialty);

        whenRefreshSpecialty(group);

        groupService.create(group);

        verify(groupRepository).save(group);
    }

    @Test
    public void shouldUpdateGroup() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        Group group = new Group("group");
        group.setCourse(1);
        group.setId(1);
        group.setSpecialty(specialty);

        when(groupRepository.getOne(group.getId())).thenReturn(group);
        whenRefreshSpecialty(group);

        groupService.update(group);

        verify(groupRepository).save(group);
    }

    @Test
    public void shouldDeleteGroup() {
        groupService.delete(1);

        verify(groupRepository).deleteById(1);
    }

    @Test
    public void shouldGetGroup() {
        Group group = new Group("group");
        group.setCourse(1);
        group.setId(1);

        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        assertEquals(group, groupService.get(1));
    }

    @Test
    public void shouldThrowExceptionWhenFindGroupByNonExistentId() {
        when(groupRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(WebNotFoundException.class, () -> groupService.get(0));
    }

    @Test
    public void shouldGetAllGroups() {
        List<Group> groups = new ArrayList<>();
        Group group1 = new Group("group1");
        group1.setCourse(1);
        group1.setId(1);
        Group group2 = new Group("group1");
        group2.setCourse(2);
        group2.setId(2);
        groups.add(group1);
        groups.add(group2);

        when(groupRepository.findAll()).thenReturn(groups);

        assertEquals(groups, groupService.getAll());
    }

    private void whenRefreshSpecialty(Group group) {
        Specialty specialty = new Specialty("specialty");
        specialty.setVersion(0L);
        specialty.setId(1);

        when(specialtyRepository.getOne(group.getSpecialty().getId())).thenReturn(specialty);
    }
}
