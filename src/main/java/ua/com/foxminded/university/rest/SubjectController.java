package ua.com.foxminded.university.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.com.foxminded.university.model.Subject;
import ua.com.foxminded.university.service.SubjectService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController("SubjectRest")
@RequestMapping("/api/subjects")
public class SubjectController {

    private SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public Slice<Subject> getAll(@PageableDefault(sort = "id") Pageable pageable) {
        log.debug("Get subjects page: {} with size: {} sorting: {}", pageable.getPageNumber(),
                pageable.getPageSize(), pageable.getSort());
        return new Slice<>(subjectService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public Subject get(@PathVariable int id) {
        log.debug("Get subject by id: {}", id);
        return subjectService.get(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Subject subject) {
        log.debug("Create subject: {}", subject);
        subjectService.create(subject);
        String location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(subject.getId())
                .toUriString();

        return ResponseEntity.status(CREATED).header(HttpHeaders.LOCATION, location).build();
    }

    @PutMapping("/{id}")
    public Subject update(@Valid @RequestBody Subject subject, @PathVariable Integer id) {
        subject.setId(id);
        log.debug("Update subject: {}", subject);
        return subjectService.update(subject);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        subjectService.get(id);
        subjectService.delete(id);
        log.debug("Delete subject by id: {}", id);
    }
}
