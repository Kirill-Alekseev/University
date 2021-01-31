package ua.com.foxminded.university.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.foxminded.university.model.Specialty;
import ua.com.foxminded.university.service.FacultyService;
import ua.com.foxminded.university.service.SpecialtyService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/specialties")
public class SpecialtyController {

    private SpecialtyService specialtyService;
    private FacultyService facultyService;

    public SpecialtyController(SpecialtyService specialtyService, FacultyService facultyService) {
        this.specialtyService = specialtyService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public ModelAndView getSpecialties(Model model) {
        log.debug("Returning specialties view");
        ModelAndView modelView = new ModelAndView("specialties/all");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("specialties", specialtyService.getAll());
        modelView.addObject("specialty", new Specialty());
        modelView.addObject("faculties", facultyService.getAll());
        return modelView;
    }

    @GetMapping("/{id}")
    public ModelAndView getSpecialty(@PathVariable int id, Model model) {
        log.debug("Returning specialties/{} view", id);
        ModelAndView modelView = new ModelAndView("specialties/one");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("specialty", specialtyService.get(id));
        modelView.addObject("faculties", facultyService.getAll());
        return modelView;
    }

    @DeleteMapping("/remove/{id}")
    public String removeSpecialty(@PathVariable int id) {
        log.debug("Remove specialty by id: {}", id);
        specialtyService.delete(id);
        return "redirect:/specialties";
    }

    @PostMapping("/add")
    public String createSpecialty(@Valid Specialty specialty, BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Specialty validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/specialties";
        }
        log.debug("Create specialty: {}", specialty);
        specialtyService.create(specialty);
        return "redirect:/specialties/" + specialty.getId();
    }

    @PatchMapping("/edit")
    public String editSpecialty(@Valid Specialty specialty, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Specialty validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/specialties/" + specialty.getId();
        }
        log.debug("Update specialty: {}", specialty);
        specialtyService.update(specialty);
        return "redirect:/specialties/" + specialty.getId();
    }
}
