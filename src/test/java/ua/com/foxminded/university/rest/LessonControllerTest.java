package ua.com.foxminded.university.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.exceptions.UniversityExceptionHandler;
import ua.com.foxminded.university.model.*;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.service.LessonService;
import ua.com.foxminded.university.service.TeacherService;
import ua.com.foxminded.university.util.JsonUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LessonControllerTest {

    @Mock
    private LessonService lessonService;
    @Mock
    private GroupService groupService;
    @Mock
    private TeacherService teacherService;
    @InjectMocks
    private LessonController lessonController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(lessonController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new UniversityExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturnCorrectLessons() throws Exception {
        List<Lesson> expected = getExpectedLessons();

        when(lessonService.getAll(PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")))))
                .thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/lessons/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Lesson> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Lesson>>() {
        });

        assertEquals(new Slice<>(expected), actual);
    }

    @Test
    public void shouldReturnCorrectLesson() throws Exception {
        Lesson expected = getExpectedLesson();

        when(lessonService.get(1)).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/lessons/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Lesson actual = JsonUtil.fromJson(contentAsString, Lesson.class);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenFindLessonByNonExistentId() throws Exception {
        when(lessonService.get(0)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(get("/api/lessons/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateLesson() throws Exception {
        Lesson lesson = getExpectedLesson();
        lesson.setId(null);

        mockMvc.perform(post("/api/lessons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJsonString(lesson)))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .containsHeader("location");

        verify(lessonService).create(lesson);
    }

    @Test
    public void shouldNotCreateInvalidLesson() throws Exception {
        Lesson lesson = new Lesson();
        lesson.setDate(LocalDate.of(2019, 1, 1));

        mockMvc.perform(post("/api/lessons")
                .characterEncoding("UTF8").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJsonString(lesson)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.subject", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.date", is("Lesson should be in future")))
                .andExpect(jsonPath("$.errors.classroom", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.group", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.teacher", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.lessonTime", is("Field is mandatory")));

        verify(lessonService, never()).create(any(Lesson.class));
    }

    @Test
    public void shouldUpdateLesson() throws Exception {
        Lesson expected = getExpectedLesson();
        expected.setId(null);

        when(lessonService.update(getExpectedLesson())).thenReturn(getExpectedLesson());

        String contentAsString = mockMvc.perform(put("/api/lessons/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJsonString(expected)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Lesson actual = JsonUtil.fromJson(contentAsString, Lesson.class);

        assertEquals(getExpectedLesson(), actual);
    }

    @Test
    public void shouldNotUpdateWithInvalidLesson() throws Exception {
        Lesson lesson = new Lesson();
        lesson.setDate(LocalDate.of(2019, 1, 1));

        mockMvc.perform(put("/api/lessons/{id}", 1)
                .characterEncoding("UTF8").contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJsonString(lesson)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.subject", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.date", is("Lesson should be in future")))
                .andExpect(jsonPath("$.errors.classroom", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.group", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.teacher", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.lessonTime", is("Field is mandatory")));

        verify(lessonService, never()).update(any(Lesson.class));
    }

    @Test
    public void shouldDeleteLesson() throws Exception {
        when(lessonService.get(1)).thenReturn(new Lesson());

        mockMvc.perform(delete("/api/lessons/{id}", 1))
                .andExpect(status().is2xxSuccessful());

        verify(lessonService).delete(1);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenDeleteLessonByNonExistentId() throws Exception {
        when(lessonService.get(1)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(delete("/api/lessons/{id}", 1))
                .andExpect(status().is(404));

        verify(lessonService, never()).delete(1);
    }

    @Test
    public void shouldGetGroupMonthSchedule() throws Exception {
        when(lessonService.getByGroupBetweenTwoDates(new Group(1),
                LocalDate.of(2020, 9, 1),
                LocalDate.of(2020, 9, 30)))
                .thenReturn(getExpectedLessons());
        when(groupService.get(1)).thenReturn(new Group(1));

        String contentAsString = mockMvc.perform(get("/api/lessons/")
                .param("yearMonth", "2020-09")
                .param("group", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Lesson> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Lesson>>() {
        });

        assertEquals(new Slice<>(getExpectedLessons()), actual);
    }

    @Test
    public void shouldGetGroupDaySchedule() throws Exception {
        when(lessonService.getByGroupAndDate(new Group(1),
                LocalDate.of(2020, 9, 1)))
                .thenReturn(getExpectedLessons());
        when(groupService.get(1)).thenReturn(new Group(1));

        String contentAsString = mockMvc.perform(get("/api/lessons/")
                .param("date", "2020-09-01")
                .param("group", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Lesson> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Lesson>>() {
        });

        assertEquals(new Slice<>(getExpectedLessons()), actual);
    }

    @Test
    public void shouldGetTeacherMonthSchedule() throws Exception {
        when(lessonService.getByTeacherBetweenTwoDates(new Teacher(1),
                LocalDate.of(2020, 9, 1),
                LocalDate.of(2020, 9, 30)))
                .thenReturn(getExpectedLessons());
        when(teacherService.get(1)).thenReturn(new Teacher(1));

        String contentAsString = mockMvc.perform(get("/api/lessons/")
                .param("yearMonth", "2020-09")
                .param("teacher", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Lesson> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Lesson>>() {
        });

        assertEquals(new Slice<>(getExpectedLessons()), actual);
    }

    @Test
    public void shouldGetTeacherDaySchedule() throws Exception {
        when(lessonService.getByTeacherAndDate(new Teacher(1),
                LocalDate.of(2020, 9, 1)))
                .thenReturn(getExpectedLessons());
        when(teacherService.get(1)).thenReturn(new Teacher(1));

        String contentAsString = mockMvc.perform(get("/api/lessons")
                .param("date", "2020-09-01")
                .param("teacher", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<Lesson> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<Lesson>>() {
        });

        assertEquals(new Slice<>(getExpectedLessons()), actual);
    }

    private List<Lesson> getExpectedLessons() {
        List<Lesson> lessons = new ArrayList<>();
        Lesson lesson1 = new Lesson();
        lesson1.setId(1);
        lesson1.setClassroom(new Classroom(1, "111", 10));
        lesson1.setDate(LocalDate.of(2030, 10, 17));
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

    private String getGroupYearMonth() {
        return "{\n" +
                "    \"group\": {\n" +
                "        \"id\": 1\n" +
                "    },\n" +
                "    \"yearMonth\": \"2020-09\"\n" +
                "}";
    }

    private String getGroupDate() {
        return "{\n" +
                "    \"group\": {\n" +
                "        \"id\": 1\n" +
                "    },\n" +
                "    \"date\": \"2020-09-01\"\n" +
                "}";
    }

    private String getTeacherYearMonth() {
        return "{\n" +
                "    \"teacher\": {\n" +
                "        \"id\": 1\n" +
                "    },\n" +
                "    \"yearMonth\": \"2020-09\"\n" +
                "}";
    }

    private String getTeacherDate() {
        return "{\n" +
                "    \"teacher\": {\n" +
                "        \"id\": 1\n" +
                "    },\n" +
                "    \"date\": \"2020-09-01\"\n" +
                "}";
    }
}
