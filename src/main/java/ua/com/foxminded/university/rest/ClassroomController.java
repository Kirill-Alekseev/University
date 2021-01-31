package ua.com.foxminded.university.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.service.ClassroomService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController("ClassroomRest")
@RequestMapping("/api/classrooms")
public class ClassroomController {

    private ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping
    public Slice<Classroom> getAll(@PageableDefault(sort = "id") Pageable pageable) {
        log.debug("Get classrooms page: {} with size: {} sorting: {}", pageable.getPageNumber(),
                pageable.getPageSize(), pageable.getSort());
        return new Slice<>(classroomService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public Classroom get(@PathVariable int id) {
        log.debug("Get classroom by id: {}", id);
        return classroomService.get(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Classroom classroom) {
        log.debug("Create classroom: {}", classroom);
        classroomService.create(classroom);
        String location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(classroom.getId())
                .toUriString();

        return ResponseEntity.status(CREATED).header(HttpHeaders.LOCATION, location).build();
    }

    @PutMapping("/{id}")
    public Classroom update(@Valid @RequestBody Classroom classroom, @PathVariable Integer id) {
        classroom.setId(id);
        log.debug("Update classroom: {}", classroom);
        return classroomService.update(classroom);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        classroomService.get(id);
        classroomService.delete(id);
        log.debug("Delete classroom by id: {}", id);
    }
}
