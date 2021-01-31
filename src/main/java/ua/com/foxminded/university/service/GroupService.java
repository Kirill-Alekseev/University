package ua.com.foxminded.university.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Specialty;
import ua.com.foxminded.university.repository.GroupRepository;
import ua.com.foxminded.university.repository.SpecialtyRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
public class GroupService {

    private GroupRepository groupRepository;
    private SpecialtyRepository specialtyRepository;

    public GroupService(GroupRepository groupRepository, SpecialtyRepository specialtyRepository) {
        this.groupRepository = groupRepository;
        this.specialtyRepository = specialtyRepository;
    }

    public List<Group> getAll() {
        log.debug("Get all groups");
        return groupRepository.findAll();
    }

    public List<Group> getAll(Pageable pageable) {
        log.debug("Get groups page: {} with size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return groupRepository.findAll(pageable).getContent();
    }

    public Group get(int id) {
        log.debug("Get group by id: {}", id);
        return groupRepository.findById(id)
                .orElseThrow(() -> new WebNotFoundException("Can't find group by passed id: %d", id));
    }

    public void create(Group group) {
        refreshSpecialty(group);

        groupRepository.save(group);
        log.debug("Create group {}", group);
    }

    public Group update(Group group) {
        Group updated = groupRepository.getOne(group.getId());
        updated.setName(group.getName());
        updated.setCourse(group.getCourse());
        updated.setSpecialty(group.getSpecialty());
        refreshSpecialty(updated);

        groupRepository.save(updated);
        log.debug("Update group {}", updated);
        return updated;
    }

    public void delete(int id) {
        groupRepository.deleteById(id);
        log.debug("Delete group by id: {}", id);
    }

    private void refreshSpecialty(Group group) {
        Specialty specialty = specialtyRepository.getOne(group.getSpecialty().getId());
        group.setSpecialty(specialty);
    }
}
