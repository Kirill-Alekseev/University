package ua.com.foxminded.university.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Cathedra;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.repository.CathedraRepository;
import ua.com.foxminded.university.repository.FacultyRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
public class CathedraService {

    private CathedraRepository cathedraRepository;
    private FacultyRepository facultyRepository;

    public CathedraService(CathedraRepository cathedraRepository, FacultyRepository facultyRepository) {
        this.cathedraRepository = cathedraRepository;
        this.facultyRepository = facultyRepository;
    }

    public List<Cathedra> getAll() {
        log.debug("Get all cathedras");
        return cathedraRepository.findAll();
    }

    public Cathedra get(int id) {
        log.debug("Get cathedra by id: {}", id);
        return cathedraRepository.findById(id)
                .orElseThrow(() -> new WebNotFoundException("Can't find cathedra by passed id: %d", id));
    }

    public void create(Cathedra cathedra) {
        refreshFaculty(cathedra);

        cathedraRepository.save(cathedra);
        log.debug("Create cathedra {}", cathedra);
    }

    public Cathedra update(Cathedra cathedra) {
        Cathedra updated = cathedraRepository.getOne(cathedra.getId());
        updated.setName(cathedra.getName());
        updated.setFaculty(cathedra.getFaculty());
        refreshFaculty(updated);

        cathedraRepository.save(updated);
        log.debug("Update cathedra {}", updated);
        return updated;
    }

    public void delete(int id) {
        cathedraRepository.deleteById(id);
        log.debug("Delete cathedra by id: {}", id);
    }

    private void refreshFaculty(Cathedra cathedra) {
        Faculty faculty = facultyRepository.getOne(cathedra.getFaculty().getId());
        cathedra.setFaculty(faculty);
    }
}
