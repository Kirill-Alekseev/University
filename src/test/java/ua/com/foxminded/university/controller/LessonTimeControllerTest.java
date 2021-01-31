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
import ua.com.foxminded.university.model.LessonTime;
import ua.com.foxminded.university.service.LessonTimeService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class LessonTimeControllerTest {

    @Mock
    private LessonTimeService lessonTimeService;
    @InjectMocks
    private LessonTimeController lessonTimeController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(lessonTimeController).build();
    }

    @Test
    public void shouldReturnLessonTimes() throws Exception {
        when(lessonTimeService.getAll()).thenReturn(getExpectedLessonTimes());

        mockMvc.perform(get("/lessonTimes"))
                .andExpect(view().name("lessonTimes/all"))
                .andExpect(model().attribute("lessonTimes", getExpectedLessonTimes()));
    }

    @Test
    public void shouldReturnLessonTime() throws Exception {
        when(lessonTimeService.get(1)).thenReturn(getExpectedLessonTime());

        mockMvc.perform(get("/lessonTimes/{id}", 1))
                .andExpect(view().name("lessonTimes/one"))
                .andExpect(model().attribute("lessonTime", getExpectedLessonTime()));
    }

    @Test
    public void shouldDeleteLessonTime() throws Exception {
        mockMvc.perform(delete("/lessonTimes/remove/{id}", 1))
                .andExpect(redirectedUrl("/lessonTimes"));

        verify(lessonTimeService).delete(1);
    }

    @Test
    public void shouldCreateLessonTime() throws Exception {
        LessonTime lessonTime = getExpectedLessonTime();
        lessonTime.setId(null);

        mockMvc.perform(post("/lessonTimes/add")
                .param("beginTime", lessonTime.getBeginTime().toString())
                .param("endTime", lessonTime.getEndTime().toString()))
                .andExpect(redirectedUrl("/lessonTimes/" + lessonTime.getId()));

        verify(lessonTimeService).create(lessonTime);
    }

    @Test
    public void shouldNotCreateInvalidLessonTime() throws Exception {
        mockMvc.perform(post("/lessonTimes/add"))
                .andExpect(redirectedUrl("/lessonTimes"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".lessonTime"));

        verify(lessonTimeService, never()).update(any(LessonTime.class));
    }

    @Test
    public void shouldEditLessonTime() throws Exception {
        LessonTime lessonTime = getExpectedLessonTime();

        mockMvc.perform(patch("/lessonTimes/edit")
                .param("id", lessonTime.getId().toString())
                .param("beginTime", lessonTime.getBeginTime().toString())
                .param("endTime", lessonTime.getEndTime().toString()))
                .andExpect(redirectedUrl("/lessonTimes/" + lessonTime.getId()));

        verify(lessonTimeService).update(lessonTime);
    }

    @Test
    public void shouldNotEditWithInvalidLessonTime() throws Exception {
        mockMvc.perform(patch("/lessonTimes/edit")
                .param("id", "1"))
                .andExpect(redirectedUrl("/lessonTimes/1"))
                .andExpect(flash().attributeExists(BindingResult.class.getName() + ".lessonTime"));

        verify(lessonTimeService, never()).update(any(LessonTime.class));
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
