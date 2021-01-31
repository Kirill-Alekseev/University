package ua.com.foxminded.university.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.service.FacultyService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RestController("FacultyRest")
@RequestMapping("/api/faculties")
public class FacultyController {

    private FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public Slice<Faculty> getAll() {
        log.debug("Get all faculties");
        return new Slice<>(facultyService.getAll());
    }

    @GetMapping("/{id}")
    public Faculty get(@PathVariable int id) {
        log.debug("Get faculty by id: {}", id);
        return facultyService.get(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Faculty faculty) {
        log.debug("Create faculty: {}", faculty);
        facultyService.create(faculty);
        String location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(faculty.getId())
                .toUriString();

        return ResponseEntity.status(CREATED).header(HttpHeaders.LOCATION, location).build();
    }

    @PutMapping("/{id}")
    public Faculty update(@Valid @RequestBody Faculty faculty, @PathVariable Integer id) {
        faculty.setId(id);
        log.debug("Update faculty: {}", faculty);
        return facultyService.update(faculty);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        facultyService.get(id);
        facultyService.delete(id);
        log.debug("Delete faculty by id: {}", id);
    }
}
