package ua.com.foxminded.university.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.foxminded.university.model.Subject;
import ua.com.foxminded.university.service.CathedraService;
import ua.com.foxminded.university.service.SubjectService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/subjects")
public class SubjectController {

    private SubjectService subjectService;
    private CathedraService cathedraService;

    public SubjectController(SubjectService subjectService, CathedraService cathedraService) {
        this.subjectService = subjectService;
        this.cathedraService = cathedraService;
    }

    @GetMapping
    public ModelAndView getSubjects(Model model) {
        log.debug("Returning subjects view");
        ModelAndView modelView = new ModelAndView("subjects/all");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("subjects", subjectService.getAll());
        modelView.addObject("subject", new Subject());
        modelView.addObject("cathedras", cathedraService.getAll());
        return modelView;
    }

    @GetMapping("/{id}")
    public ModelAndView getSubject(@PathVariable int id, Model model) {
        log.debug("Returning subjects/{} view", id);
        ModelAndView modelView = new ModelAndView("subjects/one");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("subject", subjectService.get(id));
        modelView.addObject("cathedras", cathedraService.getAll());
        return modelView;
    }

    @DeleteMapping("/remove/{id}")
    public String removeSubject(@PathVariable int id) {
        log.debug("Delete subject by id: {}", id);
        subjectService.delete(id);
        return "redirect:/subjects";
    }

    @PostMapping("/add")
    public String createSubject(@Valid Subject subject, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Subject validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/subjects";
        }
        log.debug("Create subject: {}", subject);
        subjectService.create(subject);
        return "redirect:/subjects/" + subject.getId();
    }

    @PatchMapping("/edit")
    public String editSubject(@Valid Subject subject, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Subject validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/subjects/" + subject.getId();
        }
        log.debug("Update subject: {}", subject);
        subjectService.update(subject);
        return "redirect:/subjects/" + subject.getId();
    }
}
