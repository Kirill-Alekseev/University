package ua.com.foxminded.university.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.com.foxminded.university.model.Teacher;
import ua.com.foxminded.university.service.TeacherService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController("TeacherRest")
@RequestMapping("/api/teachers")
public class TeacherController {

    private TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public Slice<Teacher> getAll(@PageableDefault(sort = "id") Pageable pageable) {
        log.debug("Get teachers page: {} with size: {} sorting: {}", pageable.getPageNumber(),
                pageable.getPageSize(), pageable.getSort());
        return new Slice<>(teacherService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public Teacher get(@PathVariable int id) {
        log.debug("Get teacher by id: {}", id);
        return teacherService.get(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Teacher teacher) {
        log.debug("Create teacher: {}", teacher);
        teacherService.create(teacher);
        String location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(teacher.getId())
                .toUriString();

        return ResponseEntity.status(CREATED).header(HttpHeaders.LOCATION, location).build();
    }

    @PutMapping("/{id}")
    public Teacher update(@Valid @RequestBody Teacher teacher, @PathVariable Integer id) {
        teacher.setId(id);
        log.debug("Update teacher: {}", teacher);
        return teacherService.update(teacher);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        teacherService.get(id);
        teacherService.delete(id);
        log.debug("Delete teacher by id: {}", id);
    }
}
