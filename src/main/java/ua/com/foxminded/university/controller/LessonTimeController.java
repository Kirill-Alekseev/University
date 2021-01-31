package ua.com.foxminded.university.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.foxminded.university.exceptions.DomainException;
import ua.com.foxminded.university.model.LessonTime;
import ua.com.foxminded.university.service.LessonTimeService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/lessonTimes")
public class LessonTimeController {

    private LessonTimeService lessonTimeService;

    public LessonTimeController(LessonTimeService lessonTimeService) {
        this.lessonTimeService = lessonTimeService;
    }

    @GetMapping
    public ModelAndView getLessonTimes(Model model) {
        log.debug("Returning lessonTimes view");
        ModelAndView modelView = new ModelAndView("lessonTimes/all");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("lessonTimes", lessonTimeService.getAll());
        modelView.addObject("lessonTime", new LessonTime());
        return modelView;
    }

    @GetMapping("/{id}")
    public ModelAndView getLessonTime(@PathVariable int id, Model model) {
        log.debug("Returning lessonTimes/{} view", id);
        ModelAndView modelView = new ModelAndView("lessonTimes/one");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("lessonTime", lessonTimeService.get(id));
        return modelView;
    }

    @DeleteMapping("/remove/{id}")
    public String removeLessonTime(@PathVariable int id) {
        log.debug("Delete lessonTime by id: {}", id);
        lessonTimeService.delete(id);
        return "redirect:/lessonTimes";
    }

    @PostMapping("/add")
    public String createLessonTime(@Valid LessonTime lessonTime, BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) throws DomainException {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("LessonTime validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/lessonTimes";
        }
        log.debug("Create lessonTime: {}", lessonTime);
        lessonTimeService.create(lessonTime);
        return "redirect:/lessonTimes/" + lessonTime.getId();
    }

    @PatchMapping("/edit")
    public String editLessonTime(@Valid LessonTime lessonTime, BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) throws DomainException {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("LessonTime validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/lessonTimes/" + lessonTime.getId();
        }
        log.debug("Update lessonTime: {}", lessonTime);
        lessonTimeService.update(lessonTime);
        return "redirect:/lessonTimes/" + lessonTime.getId();
    }
}
