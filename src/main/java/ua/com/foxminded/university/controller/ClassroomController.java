package ua.com.foxminded.university.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.service.ClassroomService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@Controller
@RequestMapping("/classrooms")
public class ClassroomController {

    private ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping
    public ModelAndView getClassrooms(Model model) {
        log.debug("Returning classrooms view");
        ModelAndView modelView = new ModelAndView("classrooms/all");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("classrooms", classroomService.getAll());
        modelView.addObject("classroom", new Classroom());
        return modelView;
    }

    @GetMapping("/{id}")
    public ModelAndView getClassroom(@PathVariable int id, Model model) {
        log.debug("Returning classrooms/{} view", id);
        ModelAndView modelView = new ModelAndView("classrooms/one");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("classroom", classroomService.get(id));
        return modelView;
    }

    @DeleteMapping("/remove/{id}")
    public String removeClassroom(@PathVariable @Min(1) int id) {
        log.debug("Delete classroom by id: {}", id);
        classroomService.delete(id);
        return "redirect:/classrooms";
    }

    @PostMapping("/add")
    public String createClassroom(@Valid Classroom classroom, BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Classroom validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel()
                    .forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/classrooms";
        }
        log.debug("Create classroom: {}", classroom);
        classroomService.create(classroom);
        return "redirect:/classrooms/" + classroom.getId();
    }

    @PatchMapping("/edit")
    public String updateClassroom(@Valid Classroom classroom, BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Classroom validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel()
                    .forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/classrooms/" + classroom.getId();
        }
        log.debug("Update classroom: {}", classroom);
        classroomService.update(classroom);
        return "redirect:/classrooms/" + classroom.getId();
    }

}
