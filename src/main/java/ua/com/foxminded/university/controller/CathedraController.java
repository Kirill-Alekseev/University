package ua.com.foxminded.university.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.foxminded.university.model.Cathedra;
import ua.com.foxminded.university.service.CathedraService;
import ua.com.foxminded.university.service.FacultyService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/cathedras")
public class CathedraController {

    private CathedraService cathedraService;
    private FacultyService facultyService;

    public CathedraController(CathedraService cathedraService, FacultyService facultyService) {
        this.cathedraService = cathedraService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public ModelAndView getCathedras(Model model) {
        log.debug("Returning cathedras view");
        ModelAndView modelView = new ModelAndView("cathedras/all");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("cathedras", cathedraService.getAll());
        modelView.addObject("cathedra", new Cathedra());
        modelView.addObject("faculties", facultyService.getAll());
        return modelView;
    }

    @GetMapping("/{id}")
    public ModelAndView getCathedra(@PathVariable int id, Model model) {
        log.debug("Returning cathedras/{} view", id);
        ModelAndView modelView = new ModelAndView("cathedras/one");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("cathedra", cathedraService.get(id));
        modelView.addObject("faculties", facultyService.getAll());
        return modelView;
    }

    @DeleteMapping("/remove/{id}")
    public String removeCathedra(@PathVariable int id) {
        log.debug("Delete cathedra by id: {}", id);
        cathedraService.delete(id);
        return "redirect:/cathedras";
    }

    @PostMapping("/add")
    public String createCathedra(@Valid Cathedra cathedra, BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Cathedra validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel()
                    .forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/cathedras";
        }
        log.debug("Create cathedra: {}", cathedra);
        cathedraService.create(cathedra);
        return "redirect:/cathedras/" + cathedra.getId();
    }

    @PatchMapping("/edit")
    public String editCathedra(@Valid Cathedra cathedra, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Cathedra validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel()
                    .forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/cathedras/" + cathedra.getId();
        }
        log.debug("Update cathedra: {}", cathedra);
        cathedraService.update(cathedra);
        return "redirect:/cathedras/" + cathedra.getId();
    }
}
