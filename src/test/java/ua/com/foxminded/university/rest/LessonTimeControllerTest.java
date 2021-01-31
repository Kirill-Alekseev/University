package ua.com.foxminded.university.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ua.com.foxminded.university.exceptions.WebNotFoundException;
import ua.com.foxminded.university.exceptions.UniversityExceptionHandler;
import ua.com.foxminded.university.model.LessonTime;
import ua.com.foxminded.university.service.LessonTimeService;
import ua.com.foxminded.university.util.JsonUtil;

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
public class LessonTimeControllerTest {

    @Mock
    private LessonTimeService lessonTimeService;
    @InjectMocks
    private LessonTimeController lessonTimeController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(lessonTimeController)
                .setControllerAdvice(new UniversityExceptionHandler())
                .build();
    }

    @Test
    public void shouldReturnCorrectLessonTimes() throws Exception {
        List<LessonTime> expected = getExpectedLessonTimes();

        when(lessonTimeService.getAll()).thenReturn(expected);

        String contentAsString = mockMvc.perform(get("/api/lessonTimes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Slice<LessonTime> actual = JsonUtil.fromJson(contentAsString, new TypeReference<Slice<LessonTime>>() {
        });

        assertEquals(new Slice<>(expected), actual);
    }

    @Test
    public void shouldReturnCorrectLessonTime() throws Exception {
        LessonTime expectedLessonTime = getExpectedLessonTime();

        when(lessonTimeService.get(1)).thenReturn(expectedLessonTime);

        String contentAsString = mockMvc.perform(get("/api/lessonTimes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LessonTime actual = JsonUtil.fromJson(contentAsString, LessonTime.class);

        assertEquals(expectedLessonTime, actual);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenFindLessonTimeByNonExistentId() throws Exception {
        when(lessonTimeService.get(0)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(get("/api/lessonTimes/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateLessonTime() throws Exception {
        LessonTime lessonTime = getExpectedLessonTime();
        lessonTime.setId(null);

        mockMvc.perform(post("/api/lessonTimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJsonString(lessonTime)))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .containsHeader("location");

        verify(lessonTimeService).create(lessonTime);
    }

    @Test
    public void shouldNotCreateInvalidLessonTime() throws Exception {
        mockMvc.perform(post("/api/lessonTimes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(new LessonTime())))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.beginTime", is("Field is mandatory")))
                .andExpect(jsonPath("$.errors.endTime", is("Field is mandatory")));

        verify(lessonTimeService, never()).create(any(LessonTime.class));
    }

    @Test
    public void shouldUpdateLessonTime() throws Exception {
        LessonTime expected = getExpectedLessonTime();
        expected.setId(null);

        when(lessonTimeService.update(getExpectedLessonTime())).thenReturn(getExpectedLessonTime());

        String contentAsString = mockMvc.perform(put("/api/lessonTimes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJsonString(expected)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LessonTime actual = JsonUtil.fromJson(contentAsString, LessonTime.class);

        assertEquals(getExpectedLessonTime(), actual);
    }

    @Test
    public void shouldNotUpdateWithInvalidLessonTime() throws Exception {
        LessonTime lessonTime = new LessonTime(LocalTime.of(9, 30), LocalTime.of(8, 0));

        mockMvc.perform(put("/api/lessonTimes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJsonString(lessonTime)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errors.lessonTime", is("Begin time should be earlier than end")));

        verify(lessonTimeService, never()).update(any(LessonTime.class));
    }

    @Test
    public void shouldDeleteLessonTime() throws Exception {
        when(lessonTimeService.get(1)).thenReturn(new LessonTime());

        mockMvc.perform(delete("/api/lessonTimes/{id}", 1))
                .andExpect(status().is2xxSuccessful());

        verify(lessonTimeService).delete(1);
    }

    @Test
    public void shouldReturnStatusNotFoundWhenDeleteLessonTimeByNonExistentId() throws Exception {
        when(lessonTimeService.get(1)).thenThrow(new WebNotFoundException(""));

        mockMvc.perform(delete("/api/lessonTimes/{id}", 1))
                .andExpect(status().is(404));

        verify(lessonTimeService, never()).delete(1);
    }

    private List<LessonTime> getExpectedLessonTimes() {
        List<LessonTime> lessonTimes = new ArrayList<>();
        lessonTimes.add(new LessonTime(1, LocalTime.of(8, 0), LocalTime.of(9, 30)));
        lessonTimes.add(new LessonTime(2, LocalTime.of(9, 45), LocalTime.of(11, 15)));
        return lessonTimes;
    }

    private LessonTime getExpectedLessonTime() {
        LessonTime lessonTime = new LessonTime(1, LocalTime.of(8, 0), LocalTime.of(9, 30));
        lessonTime.setId(1);
        return lessonTime;
    }
}
