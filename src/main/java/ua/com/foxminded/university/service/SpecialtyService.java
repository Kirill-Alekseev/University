package ua.com.foxminded.university.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.model.Specialty;
import ua.com.foxminded.university.repository.FacultyRepository;
import ua.com.foxminded.university.repository.SpecialtyRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
public class SpecialtyService {

    private SpecialtyRepository specialtyRepository;
    private FacultyRepository facultyRepository;

    public SpecialtyService(SpecialtyRepository specialtyRepository, FacultyRepository facultyRepository) {
        this.specialtyRepository = specialtyRepository;
        this.facultyRepository = facultyRepository;
    }

    public List<Specialty> getAll() {
        log.debug("Get all specialties");
        return specialtyRepository.findAll();
    }

    public Specialty get(int id) {
        log.debug("Get specialty by id: {}", id);
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new WebNotFoundException("Can't find specialty by passed id: %d", id));
    }

    public void create(Specialty specialty) {
        refreshFaculty(specialty);

        specialtyRepository.save(specialty);
        log.debug("Create specialty {}", specialty);
    }

    public Specialty update(Specialty specialty) {
        Specialty updated = specialtyRepository.getOne(specialty.getId());
        updated.setFaculty(specialty.getFaculty());
        updated.setName(specialty.getName());
        refreshFaculty(updated);

        specialtyRepository.save(updated);
        log.debug("Update specialty {}", updated);
        return updated;
    }

    public void delete(int id) {
        specialtyRepository.deleteById(id);
        log.debug("Delete specialty by id: {}", id);
    }

    private void refreshFaculty(Specialty specialty) {
        Faculty faculty = facultyRepository.getOne(specialty.getFaculty().getId());
        specialty.setFaculty(faculty);
    }
}
