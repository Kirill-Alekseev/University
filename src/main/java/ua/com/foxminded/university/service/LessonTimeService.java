package ua.com.foxminded.university.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.university.exceptions.*;
import ua.com.foxminded.university.model.LessonTime;
import ua.com.foxminded.university.repository.LessonTimeRepository;

import java.time.Duration;
import java.util.List;

import static java.time.LocalTime.parse;

@Slf4j
@Service
@Transactional(rollbackFor = DomainException.class)
public class LessonTimeService {

    @Value("${university.lessons.begin}")
    private String lessonsBegin;
    @Value("${university.lessons.end}")
    private String lessonsEnd;
    @Value("${university.lessons.duration.lesson}")
    private int lessonDuration;
    @Value("${university.lessons.duration.break}")
    private int breakDuration;

    private LessonTimeRepository lessonTimeRepository;

    public LessonTimeService(LessonTimeRepository lessonTimeRepository) {
        this.lessonTimeRepository = lessonTimeRepository;
    }

    public List<LessonTime> getAll() {
        log.debug("Get all lessonTimes");
        return lessonTimeRepository.findAll();
    }

    public LessonTime get(int id) {
        log.debug("Get lessonTime by id: {}", id);
        return lessonTimeRepository.findById(id)
                .orElseThrow(() -> new WebNotFoundException("Can't find lessonTime by passed id: %d", id));
    }

    public void create(LessonTime lessonTime) throws DomainException {
        verifyTimeCorrectToBeUploaded(lessonTime);

        lessonTimeRepository.save(lessonTime);
        log.debug("Create lessonTime {}", lessonTime);
    }

    public LessonTime update(LessonTime lessonTime) throws DomainException {
        LessonTime updated = lessonTimeRepository.getOne(lessonTime.getId());
        updated.setBeginTime(lessonTime.getBeginTime());
        updated.setEndTime(lessonTime.getEndTime());
        verifyTimeCorrectToBeUploaded(updated);

        lessonTimeRepository.save(updated);
        log.debug("Update lessonTime {}", updated);
        return updated;
    }

    public void delete(int id) {
        lessonTimeRepository.deleteById(id);
        log.debug("Delete lessonTime by id: {}", id);
    }

    private void verifyTimeCorrectToBeUploaded(LessonTime lessonTime) throws DomainException {
        verifyTimeNotOverlapsWithOthers(lessonTime);
        verifyTimeIsNotOutOfSchedule(lessonTime);
        verifyLessonDurationCorrect(lessonTime);
    }

    private void verifyTimeNotOverlapsWithOthers(LessonTime lessonTime) throws TimeOverlapsException {
        boolean overlaps = lessonTimeRepository.findAll().stream()
                .anyMatch(time -> lessonTime.getBeginTime().equals(time.getBeginTime())
                        && lessonTime.getEndTime().equals(time.getEndTime()));
        if (overlaps)
            throw new TimeOverlapsException("LessonTime overlaps with others");
    }

    private void verifyTimeIsNotOutOfSchedule(LessonTime lessonTime) throws TimeIsOutOfSchedule {
        boolean onSchedule = (lessonTime.getBeginTime().isAfter(parse(lessonsBegin))
                || lessonTime.getBeginTime().equals(parse(lessonsBegin)))
                && (lessonTime.getEndTime().isBefore(parse(lessonsEnd))
                || lessonTime.getEndTime().equals(parse(lessonsEnd)));
        if (!onSchedule)
            throw new TimeIsOutOfSchedule("Passed lessonTime with begin: %s, end: %s is out of schedule",
                    lessonTime.getBeginTime(), lessonTime.getEndTime());
    }

    private void verifyLessonDurationCorrect(LessonTime lessonTime) throws InvalidLessonTimeException {
        long actualLessonDuration = Duration.between(lessonTime.getBeginTime(), lessonTime.getEndTime()).toMinutes();
        if (actualLessonDuration != lessonDuration)
            throw new InvalidLessonTimeException("Lesson duration should be %d minutes, but was: %d", lessonDuration,
                    actualLessonDuration);
    }

}
