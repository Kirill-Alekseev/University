package ua.com.foxminded.university.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.com.foxminded.university.exceptions.DomainException;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Lesson;
import ua.com.foxminded.university.model.Teacher;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.service.LessonService;
import ua.com.foxminded.university.service.TeacherService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController("LessonRest")
@RequestMapping("/api/lessons")
public class LessonController {

    private LessonService lessonService;
    private GroupService groupService;
    private TeacherService teacherService;

    public LessonController(LessonService lessonService, GroupService groupService, TeacherService teacherService) {
        this.lessonService = lessonService;
        this.groupService = groupService;
        this.teacherService = teacherService;
    }

    @GetMapping
    public Slice<Lesson> getAll(@PageableDefault(sort = "id") Pageable pageable) {
        log.debug("Get lessons page: {} with size: {} sorting: {}", pageable.getPageNumber(),
                pageable.getPageSize(), pageable.getSort());
        return new Slice<>(lessonService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public Lesson get(@PathVariable int id) {
        log.debug("Get lesson by id: {}", id);
        return lessonService.get(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody Lesson lesson) throws DomainException {
        log.debug("Create lesson: {}", lesson);
        lessonService.create(lesson);
        String location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(lesson.getId())
                .toUriString();

        return ResponseEntity.status(CREATED).header(HttpHeaders.LOCATION, location).build();
    }

    @PutMapping("/{id}")
    public Lesson update(@Valid @RequestBody Lesson lesson, @PathVariable Integer id) throws DomainException {
        lesson.setId(id);
        log.debug("Update lesson: {}", lesson);
        return lessonService.update(lesson);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable int id) {
        lessonService.get(id);
        lessonService.delete(id);
        log.debug("Delete lesson by id: {}", id);
    }

    @GetMapping(params = {"group", "yearMonth"})
    public Slice<Lesson> getGroupMonthSchedule(@RequestParam("group") Integer groupId,
                                               @RequestParam("yearMonth") YearMonth yearMonth) {
        Group group = groupService.get(groupId);
        LocalDate fromDate = yearMonth.atDay(1);
        LocalDate toDate = yearMonth.atEndOfMonth();
        log.debug("Get group month schedule by group: {} and yearMonth: {}", group, yearMonth);
        return new Slice<>(lessonService.getByGroupBetweenTwoDates(group, fromDate, toDate));
    }

    @GetMapping(params = {"group", "date"})
    public Slice<Lesson> getGroupDaySchedule(@RequestParam("group") Integer groupId,
                                             @RequestParam("date")
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Group group = groupService.get(groupId);
        log.debug("Get group day schedule by group: {} and date: {}", group, date);
        return new Slice<>(lessonService.getByGroupAndDate(group, date));
    }

    @GetMapping(params = {"teacher", "yearMonth"})
    public Slice<Lesson> getTeacherMonthSchedule(@RequestParam("teacher") Integer teacherId,
                                                 @RequestParam("yearMonth") YearMonth yearMonth) {
        Teacher teacher = teacherService.get(teacherId);
        LocalDate fromDate = yearMonth.atDay(1);
        LocalDate toDate = yearMonth.atEndOfMonth();
        log.debug("Get teacher month schedule by teacher: {} and yearMonth: {}", teacher, yearMonth);
        return new Slice<>(lessonService.getByTeacherBetweenTwoDates(teacher, fromDate, toDate));
    }

    @GetMapping(params = {"teacher", "date"})
    public Slice<Lesson> getTeacherDaySchedule(@RequestParam("teacher") Integer teacherId,
                                               @RequestParam("date")
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Teacher teacher = teacherService.get(teacherId);
        log.debug("Get teacher day schedule by teacher: {} and date: {}", teacher, date);
        return new Slice<>(lessonService.getByTeacherAndDate(teacher, date));
    }

}
