package ua.com.foxminded.university.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.com.foxminded.university.model.Cathedra;
import ua.com.foxminded.university.service.CathedraService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController("CathedraRest")
@RequestMapping("/api/cathedras")
public class CathedraController {

    private CathedraService cathedraService;

    public CathedraController(CathedraService cathedraService) {
        this.cathedraService = cathedraService;
    }

    @GetMapping
    public Slice<Cathedra> getAll() {
        log.debug("Get all cathedras");
        return new Slice<>(cathedraService.getAll());
    }

    @GetMapping("/{id}")
    public Cathedra get(@PathVariable int id) {
        log.debug("Get cathedra by id: {}", id);
        return cathedraService.get(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Cathedra cathedra) {
        log.debug("Create cathedra: {}", cathedra);
        cathedraService.create(cathedra);
        String location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cathedra.getId())
                .toUriString();

        return ResponseEntity.status(CREATED).header(HttpHeaders.LOCATION, location).build();
    }

    @PutMapping("/{id}")
    public Cathedra update(@Valid @RequestBody Cathedra cathedra, @PathVariable Integer id) {
        cathedra.setId(id);
        log.debug("Update cathedra: {}", cathedra);
        return cathedraService.update(cathedra);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        cathedraService.get(id);
        cathedraService.delete(id);
        log.debug("Delete cathedra by id: {}", id);
    }
}
