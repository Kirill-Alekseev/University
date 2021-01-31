package ua.com.foxminded.university.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.foxminded.university.model.Teacher;
import ua.com.foxminded.university.service.SubjectService;
import ua.com.foxminded.university.service.TeacherService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private TeacherService teacherService;
    private SubjectService subjectService;

    public TeacherController(TeacherService teacherService, SubjectService subjectService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
    }

    @GetMapping
    public ModelAndView getTeachers(Model model) {
        log.debug("Returning teachers view");
        ModelAndView modelView = new ModelAndView("teachers/all");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("teachers", teacherService.getAll());
        modelView.addObject("teacher", new Teacher());
        return modelView;
    }

    @GetMapping("/{id}")
    public ModelAndView getTeacher(@PathVariable int id, Model model) {
        log.debug("Returning teachers/{} view", id);
        ModelAndView modelView = new ModelAndView("teachers/one");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("teacher", teacherService.get(id));
        modelView.addObject("subjects", subjectService.getAll());
        return modelView;
    }

    @DeleteMapping("/remove/{id}")
    public String removeTeacher(@PathVariable int id) {
        log.debug("Delete teacher by id: {}", id);
        teacherService.delete(id);
        return "redirect:/teachers";
    }

    @PostMapping("/add")
    public String createTeacher(@Valid Teacher teacher, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Teacher validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/teachers";
        }
        log.debug("Create teacher: {}", teacher);
        teacherService.create(teacher);
        return "redirect:/teachers/" + teacher.getId();
    }

    @PatchMapping("/edit")
    public String editTeacher(@Valid Teacher teacher, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Teacher validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/teachers/" + teacher.getId();
        }
        log.debug("Update teacher: {}", teacher);
        teacherService.update(teacher);
        return "redirect:/teachers/" + teacher.getId();
    }

}
