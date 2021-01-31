package ua.com.foxminded.university.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ua.com.foxminded.university.exceptions.*;
import ua.com.foxminded.university.model.LessonTime;
import ua.com.foxminded.university.repository.LessonTimeRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.LocalTime.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class LessonTimeServiceTest {

    @Mock
    private static LessonTimeRepository lessonTimeRepository;
    @InjectMocks
    private static LessonTimeService lessonTimeService;

    @BeforeAll
    public static void setLessonTimeServiceProperties() {
        lessonTimeService = new LessonTimeService(lessonTimeRepository);

        setField(lessonTimeService, "lessonsBegin", "08:00");
        setField(lessonTimeService, "lessonsEnd", "18:15");
        setField(lessonTimeService, "lessonDuration", 90);
        setField(lessonTimeService, "breakDuration", 15);
    }

    @Test
    public void shouldThrowExceptionWhenCreateWithOverlappingTime() {
        LessonTime lessonTime = new LessonTime(LocalTime.of(8, 0), LocalTime.of(9, 30));

        when(lessonTimeRepository.findAll()).thenReturn(getLessonTimes());

        assertThrows(TimeOverlapsException.class, () -> lessonTimeService.create(lessonTime));

        verify(lessonTimeRepository, never()).save(lessonTime);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateWithOverlappingTime() {
        LessonTime lessonTime = new LessonTime(LocalTime.of(8, 0), LocalTime.of(9, 30));

        whenGetOneLessonTime(lessonTime);
        when(lessonTimeRepository.findAll()).thenReturn(getLessonTimes());

        assertThrows(TimeOverlapsException.class, () -> lessonTimeService.update(lessonTime));

        verify(lessonTimeRepository, never()).save(lessonTime);
    }

    @Test
    public void shouldThrowExceptionWhenCreateWithTimeOutOfSchedule() {
        LessonTime lessonTime = new LessonTime(LocalTime.of(7, 59), LocalTime.of(9, 30));

        assertThrows(TimeIsOutOfSchedule.class, () -> lessonTimeService.create(lessonTime));

        verify(lessonTimeRepository, never()).save(lessonTime);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateWithTimeOutOfSchedule() {
        LessonTime lessonTime = new LessonTime(LocalTime.of(16, 45), LocalTime.of(18, 16));

        whenGetOneLessonTime(lessonTime);

        assertThrows(TimeIsOutOfSchedule.class, () -> lessonTimeService.update(lessonTime));

        verify(lessonTimeRepository, never()).save(lessonTime);
    }

    @Test
    public void shouldThrowExceptionWhenCreateWithIncorrectDuration() {
        LessonTime lessonTime = new LessonTime(LocalTime.of(8, 0), LocalTime.of(9, 31));

        assertThrows(InvalidLessonTimeException.class, () -> lessonTimeService.create(lessonTime));

        verify(lessonTimeRepository, never()).save(lessonTime);
    }

    @Test
    public void shouldThrowExceptionWhenUpdateWithIncorrectDuration() {
        LessonTime lessonTime = new LessonTime(LocalTime.of(8, 0), LocalTime.of(9, 29));

        whenGetOneLessonTime(lessonTime);

        assertThrows(InvalidLessonTimeException.class, () -> lessonTimeService.update(lessonTime));

        verify(lessonTimeRepository, never()).save(lessonTime);
    }

    @Test
    public void shouldCreateLessonTime() throws DomainException {
        LessonTime lessonTime = new LessonTime(LocalTime.of(8, 0), LocalTime.of(9, 30));

        lessonTimeService.create(lessonTime);

        verify(lessonTimeRepository).save(lessonTime);
    }

    @Test
    public void shouldUpdateLessonTime() throws DomainException {
        LessonTime lessonTime = new LessonTime(1, LocalTime.of(8, 0), LocalTime.of(9, 30));
        lessonTime.setVersion(0L);

        whenGetOneLessonTime(lessonTime);

        lessonTimeService.update(lessonTime);

        verify(lessonTimeRepository).save(lessonTime);
    }

    @Test
    public void shouldDeleteLessonTime() {
        lessonTimeService.delete(1);

        verify(lessonTimeRepository).deleteById(1);
    }

    @Test
    public void shouldGetLessonTime() {
        LessonTime lessonTime = new LessonTime(1, LocalTime.of(8, 0), LocalTime.of(9, 30));

        when(lessonTimeRepository.findById(1)).thenReturn(Optional.of(lessonTime));

        assertEquals(lessonTime, lessonTimeService.get(1));
    }

    @Test
    public void shouldThrowExceptionWhenFindLessonTimeByNonExistentId() {
        when(lessonTimeRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(WebNotFoundException.class, () -> lessonTimeService.get(0));
    }

    @Test
    public void shouldGetLessonTimes() {
        List<LessonTime> lessonTimes = new ArrayList<>();
        LessonTime lessonTime1 = new LessonTime(LocalTime.of(8, 0), LocalTime.of(9, 30));
        lessonTime1.setId(1);
        LessonTime lessonTime2 = new LessonTime(LocalTime.of(9, 45), LocalTime.of(11, 15));
        lessonTime2.setId(2);
        lessonTimes.add(lessonTime1);
        lessonTimes.add(lessonTime2);

        when(lessonTimeRepository.findAll()).thenReturn(lessonTimes);

        assertEquals(lessonTimes, lessonTimeService.getAll());
    }

    private List<LessonTime> getLessonTimes() {
        List<LessonTime> lessonTimes = new ArrayList<>();
        int i = 0;
        String lessonsBegin = (String) ReflectionTestUtils.getField(lessonTimeService, "lessonsBegin");
        String lessonsEnd = (String) ReflectionTestUtils.getField(lessonTimeService, "lessonsEnd");
        int lessonDuration = (int) ReflectionTestUtils.getField(lessonTimeService, "lessonDuration");
        int breakDuration = (int) ReflectionTestUtils.getField(lessonTimeService, "breakDuration");
        do {
            long minutes = (lessonDuration + breakDuration) * i++;
            LocalTime begin = parse(lessonsBegin).plusMinutes(minutes);
            LocalTime end = begin.plusMinutes(lessonDuration);
            lessonTimes.add(new LessonTime(begin, end));
        } while (lessonTimes.get(lessonTimes.size() - 1).getEndTime().isBefore(parse(lessonsEnd)));
        return lessonTimes;
    }

    private void whenGetOneLessonTime(LessonTime lessonTime) {
        LessonTime returnedLessonTime = new LessonTime(1, LocalTime.of(8, 0), LocalTime.of(9, 30));
        returnedLessonTime.setVersion(0L);

        when(lessonTimeRepository.getOne(lessonTime.getId())).thenReturn(returnedLessonTime);
    }
}
