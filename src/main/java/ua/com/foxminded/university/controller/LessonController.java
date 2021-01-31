package ua.com.foxminded.university.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.foxminded.university.controller.dto.ScheduleDto;
import ua.com.foxminded.university.exceptions.DomainException;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Lesson;
import ua.com.foxminded.university.model.Teacher;
import ua.com.foxminded.university.service.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/lessons")
public class LessonController {

    private LessonService lessonService;
    private SubjectService subjectService;
    private ClassroomService classroomService;
    private LessonTimeService lessonTimeService;
    private GroupService groupService;
    private TeacherService teacherService;

    public LessonController(LessonService lessonService, SubjectService subjectService,
                            ClassroomService classroomService, LessonTimeService lessonTimeService,
                            GroupService groupService, TeacherService teacherService) {
        this.lessonService = lessonService;
        this.subjectService = subjectService;
        this.classroomService = classroomService;
        this.lessonTimeService = lessonTimeService;
        this.groupService = groupService;
        this.teacherService = teacherService;
    }

    @GetMapping
    public ModelAndView getLessons(Model model) {
        log.debug("Returning lessons view");
        ModelAndView modelView = new ModelAndView("lessons/all");
        getModelAndView(modelView, model);
        modelView.addObject("lessons", lessonService.getAll());
        modelView.addObject("lesson", new Lesson());
        return modelView;
    }

    @GetMapping("/{id}")
    public ModelAndView getLesson(@PathVariable int id, Model model) {
        log.debug("Returning lessons/{} view", id);
        ModelAndView modelView = new ModelAndView("lessons/one");
        getModelAndView(modelView, model);
        modelView.addObject("lesson", lessonService.get(id));
        return modelView;
    }

    @DeleteMapping("/remove/{id}")
    public String removeLesson(@PathVariable int id) {
        log.debug("Delete lesson by id: {}", id);
        lessonService.delete(id);
        return "redirect:/lessons";
    }

    @PostMapping("/add")
    public String createLesson(@Valid Lesson lesson, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) throws DomainException {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Lesson validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/lessons";
        }
        log.debug("Create lesson: {}", lesson);
        lessonService.create(lesson);
        return "redirect:/lessons/" + lesson.getId();
    }

    @PatchMapping("/edit")
    public String editLesson(@Valid Lesson lesson, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws DomainException {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Lesson validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/lessons/" + lesson.getId();
        }
        log.debug("Update lesson: {}", lesson);
        lessonService.update(lesson);
        return "redirect:/lessons/" + lesson.getId();
    }

    @GetMapping("/groupMonthSchedule")
    public ModelAndView getGroupMonthSchedule(@ModelAttribute Group group, @RequestParam YearMonth yearMonth) {
        log.debug("Get group month schedule by group: {} and yearMonth: {}", group, yearMonth);
        ModelAndView modelView = new ModelAndView("lessons/groupMonthSchedule");

        LocalDate fromDate = yearMonth.atDay(1);
        LocalDate toDate = yearMonth.atEndOfMonth();

        modelView.addObject("dto", getScheduleDto(lessonService
                .getByGroupBetweenTwoDates(group, fromDate, toDate)));
        modelView.addObject("dates", getDatesOfMonth(yearMonth));
        modelView.addObject("group", group);
        return modelView;
    }

    @GetMapping("/groupDaySchedule")
    public ModelAndView getGroupDaySchedule(@ModelAttribute Group group,
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate date) {
        log.debug("Get group day schedule by group: {} and date: {}", group, date);
        ModelAndView modelView = new ModelAndView("lessons/groupDaySchedule");
        modelView.addObject("group", group);
        modelView.addObject("date", date);
        modelView.addObject("lessons", lessonService.getByGroupAndDate(group, date));
        return modelView;
    }

    @GetMapping("/teacherMonthSchedule")
    public ModelAndView getTeacherMonthSchedule(@ModelAttribute Teacher teacher, @RequestParam YearMonth yearMonth, Model model) {
        log.debug("Get teacher month schedule by teacher: {} and yearMonth: {}", teacher, yearMonth);
        ModelAndView modelView = new ModelAndView("lessons/teacherMonthSchedule");

        LocalDate fromDate = yearMonth.atDay(1);
        LocalDate toDate = yearMonth.atEndOfMonth();

        modelView.addObject("dto", getScheduleDto(lessonService
                .getByTeacherBetweenTwoDates(teacher, fromDate, toDate)));
        modelView.addObject("dates", getDatesOfMonth(yearMonth));
        modelView.addObject("teacher", teacher);
        return modelView;
    }

    @GetMapping("/teacherDaySchedule")
    public ModelAndView getTeacherDaySchedule(@ModelAttribute Teacher teacher,
                                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate date) {
        log.debug("Get teacher day schedule by teacher: {} and yearMonth: {}", teacher, date);
        ModelAndView modelView = new ModelAndView("lessons/teacherDaySchedule");
        modelView.addObject("date", date);
        modelView.addObject("teacher", teacher);
        modelView.addObject("lessons", lessonService.getByTeacherAndDate(teacher, date));
        return modelView;
    }

    private void getModelAndView(ModelAndView modelView, Model model) {
        modelView.addAllObjects(model.asMap());
        modelView.addObject("subjects", subjectService.getAll());
        modelView.addObject("classrooms", classroomService.getAll());
        modelView.addObject("lessonTimes", lessonTimeService.getAll());
        modelView.addObject("groups", groupService.getAll());
        modelView.addObject("teachers", teacherService.getAll());
    }

    private ScheduleDto getScheduleDto(List<Lesson> lessons) {
        ScheduleDto scheduleDto = new ScheduleDto();
        List<ScheduleDto> dtos = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (dtos.stream().anyMatch(day -> lesson.getDate().equals(day.getDate()))) {
                ScheduleDto dto = dtos.stream().filter(day -> lesson.getDate().equals(day.getDate())).findAny().get();
                dto.getLessons().add(lesson);
            } else {
                ScheduleDto dto = new ScheduleDto();
                dto.setDate(lesson.getDate());
                dto.getLessons().add(lesson);
                dtos.add(dto);
            }
        }
        scheduleDto.setDtos(dtos);
        return scheduleDto;
    }

    private List<LocalDate> getDatesOfMonth(YearMonth yearMonth) {
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            dates.add(LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), i));
        }
        return dates;
    }
}
