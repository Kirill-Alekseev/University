package ua.com.foxminded.university.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.com.foxminded.university.exceptions.DomainException;
import ua.com.foxminded.university.model.Student;
import ua.com.foxminded.university.service.StudentService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController("StudentRest")
@RequestMapping("/api/students")
public class StudentController {

    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public Slice<Student> getAll(@PageableDefault(sort = "id") Pageable pageable) {
        log.debug("Get students page: {} with size: {} sorting: {}", pageable.getPageNumber(),
                pageable.getPageSize(), pageable.getSort());
        return new Slice<>(studentService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public Student get(@PathVariable int id) {
        log.debug("Get student by id: {}", id);
        return studentService.get(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Student student) throws DomainException {
        log.debug("Create student: {}", student);
        studentService.create(student);
        String location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(student.getId())
                .toUriString();

        return ResponseEntity.status(CREATED).header(HttpHeaders.LOCATION, location).build();
    }

    @PutMapping("/{id}")
    public Student update(@Valid @RequestBody Student student, @PathVariable Integer id) throws DomainException {
        student.setId(id);
        log.debug("Update student: {}", student);
        return studentService.update(student);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        studentService.get(id);
        studentService.delete(id);
        log.debug("Delete student by id: {}", id);
    }
}
