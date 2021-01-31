package ua.com.foxminded.university.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.service.SpecialtyService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/groups")
public class GroupController {

    private GroupService groupService;
    private SpecialtyService specialtyService;

    public GroupController(GroupService groupService, SpecialtyService specialtyService) {
        this.groupService = groupService;
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public ModelAndView getGroups(Model model) {
        log.debug("Returning groups view");
        ModelAndView modelView = new ModelAndView("groups/all");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("groups", groupService.getAll());
        modelView.addObject("group", new Group());
        modelView.addObject("specialties", specialtyService.getAll());
        return modelView;
    }

    @GetMapping("/{id}")
    public ModelAndView getGroup(@PathVariable int id, Model model) {
        log.debug("Returning groups/{} view", id);
        ModelAndView modelView = new ModelAndView("groups/one");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("group", groupService.get(id));
        modelView.addObject("specialties", specialtyService.getAll());
        return modelView;
    }

    @DeleteMapping("remove/{id}")
    public String removeGroup(@PathVariable int id) {
        log.debug("Delete group by id: {}", id);
        groupService.delete(id);
        return "redirect:/groups";
    }

    @PostMapping("/add")
    public String createGroup(@Valid Group group, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Group validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/groups";
        }
        log.debug("Create group: {}", group);
        groupService.create(group);
        return "redirect:/groups/" + group.getId();
    }

    @PatchMapping("/edit")
    public String editGroup(@Valid Group group, BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Group validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/groups/" + group.getId();
        }
        log.debug("Update group: {}", group);
        groupService.update(group);
        return "redirect:/groups/" + group.getId();
    }

}
