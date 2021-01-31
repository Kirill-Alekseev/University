package ua.com.foxminded.university.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.repository.ClassroomRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
public class ClassroomService {

    private ClassroomRepository classroomRepository;

    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    public List<Classroom> getAll() {
        log.debug("Get all classrooms");
        return classroomRepository.findAll();
    }

    public List<Classroom> getAll(Pageable pageable) {
        log.debug("Get classrooms page: {} with size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return classroomRepository.findAll(pageable).getContent();
    }

    public Classroom get(int id) {
        log.debug("Get classroom by id: {}", id);
        return classroomRepository.findById(id)
                .orElseThrow(() -> new WebNotFoundException("Can't find classroom by passed id: %d", id));
    }

    public void create(Classroom classroom) {
        classroomRepository.save(classroom);
        log.debug("Create classroom {}", classroom);
    }

    public Classroom update(Classroom classroom) {
        Classroom updated = classroomRepository.getOne(classroom.getId());
        updated.setCapacity(classroom.getCapacity());
        updated.setName(classroom.getName());

        classroomRepository.save(updated);
        log.debug("Update classroom {}", updated);
        return updated;
    }

    public void delete(int id) {
        classroomRepository.deleteById(id);
        log.debug("Delete classroom by id: {}", id);
    }

}
