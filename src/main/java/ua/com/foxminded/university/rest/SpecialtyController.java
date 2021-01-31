package ua.com.foxminded.university.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.com.foxminded.university.model.Specialty;
import ua.com.foxminded.university.service.SpecialtyService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController("SpecialtyRest")
@RequestMapping("/api/specialties")
public class SpecialtyController {

    private SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public Slice<Specialty> getAll() {
        log.debug("Get all specialties");
        return new Slice<>(specialtyService.getAll());
    }

    @GetMapping("/{id}")
    public Specialty get(@PathVariable int id) {
        log.debug("Get specialty by id: {}", id);
        return specialtyService.get(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Specialty specialty) {
        log.debug("Create specialty: {}", specialty);
        specialtyService.create(specialty);
        String location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(specialty.getId())
                .toUriString();

        return ResponseEntity.status(CREATED).header(HttpHeaders.LOCATION, location).build();
    }

    @PutMapping("/{id}")
    public Specialty update(@Valid @RequestBody Specialty specialty, @PathVariable Integer id) {
        specialty.setId(id);
        log.debug("Update specialty: {}", specialty);
        return specialtyService.update(specialty);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        specialtyService.get(id);
        specialtyService.delete(id);
        log.debug("Delete specialty: {}", id);
    }
}
