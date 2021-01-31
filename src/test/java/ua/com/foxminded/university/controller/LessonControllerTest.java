package ua.com.foxminded.university.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import ua.com.foxminded.university.controller.dto.ScheduleDto;
import ua.com.foxminded.university.model.*;
import ua.com.foxminded.university.service.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class LessonControllerTest {

    @Mock
    private LessonService lessonService;
    @Mock
    private SubjectService subjectService;
    @Mock
    private ClassroomService classroomService;
    @Mock
    private LessonTimeService lessonTimeService;
    @Mock
    private GroupService groupService;
    @Mock
    private TeacherService teacherService;
    @InjectMocks
    private LessonController lessonController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(lessonController).build();
    }

    @Test
    public void shouldReturnLessons() throws Exception {
        when(lessonService.getAll()).thenReturn(getExpectedLessons());
        when(subjectService.getAll()).thenReturn(new ArrayList<>());
        when(classroomService.getAll()).thenReturn(new ArrayList<>());
        when(lessonTimeService.getAll()).thenReturn(new ArrayList<>());
        when(groupService.getAll()).thenReturn(new ArrayList<>());
        when(teacherService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/lessons"))
                .andExpect(view().name("lessons/all"))
                .andExpect(model().attribute("lessons", getExpectedLessons()));
    }

    @Test
    public void shouldReturnLesson() throws Exception {
        when(lessonService.get(1)).thenReturn(getExpectedLesson());
        when(subjectService.getAll()).thenReturn(new ArrayList<>());
        when(classroomService.getAll()).thenReturn(new ArrayList<>());
        when(lessonTimeService.getAll()).thenReturn(new ArrayList<>());
        when(groupService.getAll()).thenReturn(new ArrayList<>());
        when(teacherService.getAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/lessons/{id}", 1))
                .andExpect(view().name("lessons/one"))
                .andExpect(model().attribute("lesson", getExpectedLesson()));
    }

    @Test
    public void shouldDeleteLesson() throws Exception {
        mockMvc.perform(delete("/lessons/remove/{id}", 1))
                .andExpect(redirectedUrl("/lessons"));

        verify(lessonService).delete(1);
    }

    @Test
    public void shouldCreateLesson() throws Exception {
        Lesson lesson = getExpectedLesson();
        lesson.setId(null);

        mockMvc.perform(post("/lessons/add")
                .param("classroom.id", lesson.getClassroom().getId().toString())
                .param("subject.id", lesson.getSubject().getId().toString())
                .param("teacher.id", lesson.getTeacher().getId().toString())
                .param("group.id", lesson.getGroup().getId().toString())
                .param("lessonTime.id", lesson.getLessonTime().getId().toString())
                .param("date", lesson.getDate().toString()))
                .andExpect(redirectedUrl("/lessons/" + lesson.getId()));

        verify(lessonService).create(lesson);
    }

    @Test
    public void shouldNotCreateInvalidLesson() throws Exception {
        mockMvc.perform(post("/lessons/add"))
                .andExpect(redirectedUrl("/lessons"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".lesson"));

        verify(lessonService, never()).create(any(Lesson.class));
    }

    @Test
    public void shouldEditLesson() throws Exception {
        Lesson lesson = getExpectedLesson();

        mockMvc.perform(patch("/lessons/edit")
                .param("id", lesson.getId().toString())
                .param("classroom.id", lesson.getClassroom().getId().toString())
                .param("subject.id", lesson.getSubject().getId().toString())
                .param("teacher.id", lesson.getTeacher().getId().toString())
                .param("group.id", lesson.getGroup().getId().toString())
                .param("lessonTime.id", lesson.getLessonTime().getId().toString())
                .param("date", lesson.getDate().toString()))
                .andExpect(redirectedUrl("/lessons/" + lesson.getId()));

        verify(lessonService).update(lesson);
    }

    @Test
    public void shouldNotEditWithInvalidLesson() throws Exception {
        mockMvc.perform(patch("/lessons/edit")
                .param("id", "1"))
                .andExpect(redirectedUrl("/lessons/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".lesson"));

        verify(lessonService, never()).update(any(Lesson.class));
    }

    @Test
    public void shouldGetGroupMonthSchedule() throws Exception {
        when(lessonService.getByGroupBetweenTwoDates(new Group(1),
                LocalDate.of(2020, 9, 1),
                LocalDate.of(2020, 9, 30)))
                .thenReturn(getExpectedLessons());

        mockMvc.perform(get("/lessons/groupMonthSchedule")
                .param("id", "1")
                .param("yearMonth", "2020-09"))
                .andExpect(model().attribute("dto", getExpectedDto()))
                .andExpect(model().attribute("dates", getExpectedDates()))
                .andExpect(model().attribute("group", new Group(1)));
    }

    @Test
    public void shouldGetGroupDaySchedule() throws Exception {
        when(lessonService.getByGroupAndDate(new Group(1), LocalDate.of(2020, 11, 6)))
                .thenReturn(getExpectedLessons());

        mockMvc.perform(get("/lessons/groupDaySchedule")
                .param("id", "1")
                .param("date", "2020-11-06"))
                .andExpect(model().attribute("group", new Group(1)))
                .andExpect(model().attribute("lessons", getExpectedLessons()))
                .andExpect(model().attribute("date", LocalDate.of(2020, 11, 6)));
    }

    @Test
    public void shouldGetTeacherMonthSchedule() throws Exception {
        when(lessonService.getByTeacherBetweenTwoDates(new Teacher(1),
                LocalDate.of(2020, 9, 1),
                LocalDate.of(2020, 9, 30)))
                .thenReturn(getExpectedLessons());

        mockMvc.perform(get("/lessons/teacherMonthSchedule")
                .param("id", "1")
                .param("yearMonth", "2020-09"))
                .andExpect(model().attribute("dto", getExpectedDto()))
                .andExpect(model().attribute("dates", getExpectedDates()))
                .andExpect(model().attribute("teacher", new Teacher(1)));
    }

    @Test
    public void shouldGetTeacherDaySchedule() throws Exception {
        when(lessonService.getByTeacherAndDate(new Teacher(1), LocalDate.of(2020, 11, 6)))
                .thenReturn(getExpectedLessons());

        mockMvc.perform(get("/lessons/teacherDaySchedule")
                .param("id", "1")
                .param("date", "2020-11-06"))
                .andExpect(model().attribute("teacher", new Teacher(1)))
                .andExpect(model().attribute("lessons", getExpectedLessons()))
                .andExpect(model().attribute("date", LocalDate.of(2020, 11, 6)));
    }

    private List<Lesson> getExpectedLessons() {
        List<Lesson> lessons = new ArrayList<>();
        Lesson lesson1 = new Lesson();
        lesson1.setId(1);
        lesson1.setClassroom(new Classroom(1, "111", 10));
        lesson1.setDate(LocalDate.of(2020, 10, 17));
        lesson1.setGroup(new Group(1, "QW-12", 1));
        lesson1.setSubject(new Subject("subject"));
        lesson1.setTeacher(new Teacher("name1", "surname1"));
        lesson1.setLessonTime(new LessonTime(1, LocalTime.of(8, 0), LocalTime.of(9, 30)));
        Lesson lesson2 = new Lesson();
        lesson2.setId(2);
        lesson2.setClassroom(new Classroom(2, "222", 22));
        lesson2.setDate(LocalDate.of(2020, 10, 16));
        lesson2.setGroup(new Group(2, "ER-34", 2));
        lesson2.setSubject(new Subject("subject1"));
        lesson2.setTeacher(new Teacher("name2", "surname2"));
        lesson2.setLessonTime(new LessonTime(2, LocalTime.of(9, 45), LocalTime.of(11, 15)));
        lessons.add(lesson1);
        lessons.add(lesson2);
        return lessons;
    }

    private Lesson getExpectedLesson() {
        Lesson lesson = new Lesson();
        lesson.setId(1);
        lesson.setClassroom(new Classroom(1, "111", 10));
        lesson.setDate(LocalDate.of(2030, 12, 17));
        lesson.setGroup(new Group(1, "QW-12", 1));
        lesson.setSubject(new Subject("subject"));
        lesson.getSubject().setId(1);
        lesson.getSubject().setCathedra(new Cathedra());
        lesson.setTeacher(new Teacher("name1", "surname1"));
        lesson.getTeacher().setId(1);
        lesson.setLessonTime(new LessonTime());
        lesson.getLessonTime().setId(1);
        return lesson;
    }

    private ScheduleDto getExpectedDto() {
        ScheduleDto dto = new ScheduleDto();

        ScheduleDto dto1 = new ScheduleDto();
        dto1.setDate(LocalDate.of(2020, 10, 17));
        List<Lesson> lessons1 = new ArrayList<>();
        Lesson lesson1 = new Lesson();
        lesson1.setId(1);
        lesson1.setClassroom(new Classroom(1, "111", 10));
        lesson1.setDate(LocalDate.of(2020, 10, 17));
        lesson1.setGroup(new Group(1, "QW-12", 1));
        lesson1.setSubject(new Subject("subject"));
        lesson1.setTeacher(new Teacher("name1", "surname1"));
        lesson1.setLessonTime(new LessonTime(1, LocalTime.of(8, 0), LocalTime.of(9, 30)));
        lessons1.add(lesson1);
        dto1.setLessons(lessons1);

        ScheduleDto dto2 = new ScheduleDto();
        dto2.setDate(LocalDate.of(2020, 10, 16));
        List<Lesson> lessons2 = new ArrayList<>();
        Lesson lesson2 = new Lesson();
        lesson2.setId(2);
        lesson2.setClassroom(new Classroom(2, "222", 22));
        lesson2.setDate(LocalDate.of(2020, 10, 16));
        lesson2.setGroup(new Group(2, "ER-34", 2));
        lesson2.setSubject(new Subject("subject1"));
        lesson2.setTeacher(new Teacher("name2", "surname2"));
        lesson2.setLessonTime(new LessonTime(2, LocalTime.of(9, 45), LocalTime.of(11, 15)));
        lessons2.add(lesson2);
        dto2.setLessons(lessons2);

        dto.getDtos().add(dto1);
        dto.getDtos().add(dto2);

        return dto;
    }

    private List<LocalDate> getExpectedDates() {
        List<LocalDate> dates = new ArrayList<>();
        int year = 2020;
        int month = 9;
        for (int day = 1; day <= 30; day++) {
            dates.add(LocalDate.of(year, month, day));
        }
        return dates;
    }
}
