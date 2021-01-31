package ua.com.foxminded.university.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.repository.FacultyRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
public class FacultyService {

    private FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public List<Faculty> getAll() {
        log.debug("Get all faculties");
        return facultyRepository.findAll();
    }

    public Faculty get(int id) {
        log.debug("Get faculty by id: {}", id);
        return facultyRepository.findById(id)
                .orElseThrow(() -> new WebNotFoundException("Can't find faculty by passed id: %d", id));
    }

    public void create(Faculty faculty) {
        facultyRepository.save(faculty);
        log.debug("Create faculty {}", faculty);
    }

    public Faculty update(Faculty faculty) {
        Faculty updated = facultyRepository.getOne(faculty.getId());
        updated.setName(faculty.getName());

        facultyRepository.save(updated);
        log.debug("Update faculty {}", updated);
        return updated;
    }

    public void delete(int id) {
        facultyRepository.deleteById(id);
        log.debug("Delete faculty by id: {}", id);
    }

}
