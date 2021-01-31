package ua.com.foxminded.university.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Cathedra;
import ua.com.foxminded.university.model.Subject;
import ua.com.foxminded.university.model.Teacher;
import ua.com.foxminded.university.repository.SubjectRepository;
import ua.com.foxminded.university.repository.TeacherRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TeacherService {

    private TeacherRepository teacherRepository;
    private SubjectRepository subjectRepository;

    public TeacherService(TeacherRepository teacherRepository, SubjectRepository subjectRepository) {
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<Teacher> getAll() {
        log.debug("Get all teachers");
        return teacherRepository.findAll();
    }

    public List<Teacher> getAll(Pageable pageable) {
        log.debug("Get teachers page: {} with size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return teacherRepository.findAll(pageable).getContent();
    }

    public Teacher get(int id) {
        log.debug("Get teacher by id: {}", id);
        return teacherRepository.findById(id)
                .orElseThrow(() -> new WebNotFoundException("Can't find teacher by passed id: %d", id));
    }

    public void create(Teacher teacher) {
        addAbsentCathedras(teacher);

        teacherRepository.save(teacher);
        log.debug("Create teacher {}", teacher);
    }

    public Teacher update(Teacher teacher) {
        Teacher updated = teacherRepository.getOne(teacher.getId());
        updated.setSubjects(teacher.getSubjects());
        updated.setFirstName(teacher.getFirstName());
        updated.setLastName(teacher.getLastName());
        addAbsentCathedras(updated);

        teacherRepository.save(updated);
        log.debug("Update teacher {}", updated);
        return updated;
    }

    public void delete(int id) {
        teacherRepository.deleteById(id);
        log.debug("Delete teacher by id: {}", id);
    }

    private void addAbsentCathedras(Teacher teacher) {
        List<Cathedra> cathedras = teacher.getSubjects().stream().map(subject -> {
            Subject s = subjectRepository.getOne(subject.getId());
            return s.getCathedra();
        }).distinct().collect(Collectors.toList());
        teacher.setCathedras(cathedras);
    }

}
