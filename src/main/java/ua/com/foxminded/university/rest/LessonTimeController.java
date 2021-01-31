package ua.com.foxminded.university.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.com.foxminded.university.exceptions.DomainException;
import ua.com.foxminded.university.model.LessonTime;
import ua.com.foxminded.university.service.LessonTimeService;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController("LessonTimeRest")
@RequestMapping("/api/lessonTimes")
public class LessonTimeController {

    private LessonTimeService lessonTimeService;

    public LessonTimeController(LessonTimeService lessonTimeService) {
        this.lessonTimeService = lessonTimeService;
    }

    @GetMapping
    public Slice<LessonTime> getAll() {
        log.debug("Get all lessonTimes");
        return new Slice<>(lessonTimeService.getAll());
    }

    @GetMapping("/{id}")
    public LessonTime get(@PathVariable int id) {
        log.debug("Get lessonTime by id: {}", id);
        return lessonTimeService.get(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody LessonTime lessonTime) throws DomainException {
        log.debug("Create lessonTime: {}", lessonTime);
        lessonTimeService.create(lessonTime);
        String location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(lessonTime.getId())
                .toUriString();

        return ResponseEntity.status(CREATED).header(HttpHeaders.LOCATION, location).build();
    }

    @PutMapping("/{id}")
    public LessonTime update(@Valid @RequestBody LessonTime lessonTime, @PathVariable Integer id) throws DomainException {
        lessonTime.setId(id);
        log.debug("Update lessonTime: {}", lessonTime);
        return lessonTimeService.update(lessonTime);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        lessonTimeService.get(id);
        lessonTimeService.delete(id);
        log.debug("Delete lessonTime by id: {}", id);
    }
}
