package ua.com.foxminded.university.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.exceptions.DomainException;
import ua.com.foxminded.university.exceptions.GroupOverflowException;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Student;
import ua.com.foxminded.university.repository.GroupRepository;
import ua.com.foxminded.university.repository.StudentRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = DomainException.class)
public class StudentService {

    @Value("${university.students.max-students-in-group}")
    private int maxStudentsInGroup;

    private StudentRepository studentRepository;
    private GroupRepository groupRepository;

    public StudentService(StudentRepository studentRepository, GroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
    }

    public List<Student> getAll() {
        log.debug("Get all students");
        return studentRepository.findAll();
    }

    public List<Student> getAll(Pageable pageable) {
        log.debug("Get students page: {} with size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return studentRepository.findAll(pageable).getContent();
    }

    public Student get(int id) {
        log.debug("Get student by id: {}", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> new WebNotFoundException("Can't find student by passed id: %d", id));
    }

    public void create(Student student) throws DomainException {
        refreshGroup(student);
        verifyNotTooManyStudentsInGroup(student);

        studentRepository.save(student);
        log.debug("Create student {}", student);
    }

    public Student update(Student student) throws DomainException {
        Student updated = studentRepository.getOne(student.getId());
        updated.setGroup(student.getGroup());
        updated.setFirstName(student.getFirstName());
        updated.setLastName(student.getLastName());
        updated.setEmail(student.getEmail());
        updated.setBirthDate(student.getBirthDate());
        updated.setPhoneNumber(student.getPhoneNumber());
        refreshGroup(updated);
        verifyNotTooManyStudentsInGroup(updated);

        studentRepository.save(updated);
        log.debug("Update student {}", updated);
        return updated;
    }

    public void delete(int id) {
        studentRepository.deleteById(id);
        log.debug("Delete student by id: {}", id);
    }

    private void verifyNotTooManyStudentsInGroup(Student student) throws GroupOverflowException {
        int studentsCount = studentRepository.countByGroupId(student.getGroup().getId());
        if (studentsCount > maxStudentsInGroup)
            throw new GroupOverflowException("Max students in group can't be > %d, actual: %d", maxStudentsInGroup,
                    studentsCount);
    }

    private void refreshGroup(Student student) {
        Group group = groupRepository.getOne(student.getGroup().getId());
        student.setGroup(group);
    }
}
