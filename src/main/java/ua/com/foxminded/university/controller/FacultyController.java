package ua.com.foxminded.university.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.foxminded.university.model.Faculty;
import ua.com.foxminded.university.service.FacultyService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/faculties")
public class FacultyController {

    private FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public ModelAndView getFaculties(Model model) {
        log.debug("Returning faculties view");
        ModelAndView modelView = new ModelAndView("faculties/all");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("faculties", facultyService.getAll());
        modelView.addObject("faculty", new Faculty());
        return modelView;
    }

    @GetMapping("/{id}")
    public ModelAndView getFaculty(@PathVariable int id, Model model) {
        log.debug("Returning faculties/{} view", id);
        ModelAndView modelView = new ModelAndView("faculties/one");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("faculty", facultyService.get(id));
        return modelView;
    }

    @DeleteMapping("remove/{id}")
    public String removeFaculty(@PathVariable int id) {
        log.debug("Delete faculty by id: {}", id);
        facultyService.delete(id);
        return "redirect:/faculties";
    }

    @PostMapping("/add")
    public String createFaculty(@Valid Faculty faculty, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Faculty validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/faculties";
        }
        log.debug("Create faculty: {}", faculty);
        facultyService.create(faculty);
        return "redirect:/faculties/" + faculty.getId();
    }

    @PatchMapping("/edit")
    public String editFaculty(@Valid Faculty faculty, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Faculty validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/faculties/" + faculty.getId();
        }
        log.debug("Update faculty: {}", faculty);
        facultyService.update(faculty);
        return "redirect:/faculties/" + faculty.getId();
    }

}
