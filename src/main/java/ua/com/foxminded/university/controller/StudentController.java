package ua.com.foxminded.university.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.com.foxminded.university.exceptions.DomainException;
import ua.com.foxminded.university.model.Student;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.service.StudentService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/students")
public class StudentController {

    private StudentService studentService;
    private GroupService groupService;

    public StudentController(StudentService studentService, GroupService groupService) {
        this.studentService = studentService;
        this.groupService = groupService;
    }

    @GetMapping
    public ModelAndView getStudents(Model model) {
        log.debug("Returning students view");
        ModelAndView modelView = new ModelAndView("students/all");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("students", studentService.getAll());
        modelView.addObject("groups", groupService.getAll());
        modelView.addObject("student", new Student());
        return modelView;
    }

    @GetMapping("/{id}")
    public ModelAndView getStudent(@PathVariable int id, Model model) {
        log.debug("Returning students/{} view", id);
        ModelAndView modelView = new ModelAndView("students/one");
        modelView.addAllObjects(model.asMap());
        modelView.addObject("student", studentService.get(id));
        modelView.addObject("groups", groupService.getAll());
        return modelView;
    }

    @DeleteMapping("/remove/{id}")
    public String removeStudent(@PathVariable int id) {
        log.debug("Remove student by id: {}", id);
        studentService.delete(id);
        return "redirect:/students";
    }

    @PostMapping("/add")
    public String createStudent(@Valid Student student, BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) throws DomainException {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Student validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/students";
        }
        log.debug("Create student: {}", student);
        studentService.create(student);
        return "redirect:/students/" + student.getId();
    }

    @PatchMapping("/edit")
    public String editStudent(@Valid Student student, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) throws DomainException {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.debug("Student validation error: {}", error.getDefaultMessage()));
            bindingResult.getModel().
                    forEach(redirectAttributes::addFlashAttribute);
            return "redirect:/students/" + student.getId();
        }
        log.debug("Update student: {}", student);
        studentService.update(student);
        return "redirect:/students/" + student.getId();
    }
}
