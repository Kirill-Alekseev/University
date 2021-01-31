package ua.com.foxminded.university.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Cathedra;
import ua.com.foxminded.university.model.Subject;
import ua.com.foxminded.university.repository.CathedraRepository;
import ua.com.foxminded.university.repository.SubjectRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
public class SubjectService {

    private SubjectRepository subjectRepository;
    private CathedraRepository cathedraRepository;

    public SubjectService(SubjectRepository subjectRepository, CathedraRepository cathedraRepository) {
        this.subjectRepository = subjectRepository;
        this.cathedraRepository = cathedraRepository;
    }

    public List<Subject> getAll() {
        log.debug("Get all subjects");
        return subjectRepository.findAll();
    }

    public List<Subject> getAll(Pageable pageable) {
        log.debug("Get subjects page: {} with size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return subjectRepository.findAll(pageable).getContent();
    }

    public Subject get(int id) {
        log.debug("Get subject by id: {}", id);
        return subjectRepository.findById(id)
                .orElseThrow(() -> new WebNotFoundException("Can't find subject by passed id: %d", id));
    }

    public void create(Subject subject) {
        refreshCathedra(subject);

        subjectRepository.save(subject);
        log.debug("Create subject {}", subject);
    }

    public Subject update(Subject subject) {
        Subject updated = subjectRepository.getOne(subject.getId());
        updated.setCathedra(subject.getCathedra());
        updated.setName(subject.getName());
        refreshCathedra(updated);

        subjectRepository.save(updated);
        log.debug("Update subject {}", updated);
        return updated;
    }

    public void delete(int id) {
        subjectRepository.deleteById(id);
        log.debug("Delete subject by id: {}", id);
    }

    private void refreshCathedra(Subject subject) {
        Cathedra cathedra = cathedraRepository.getOne(subject.getCathedra().getId());
        subject.setCathedra(cathedra);
    }
}
