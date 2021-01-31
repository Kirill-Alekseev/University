package ua.com.foxminded.university.controller.dto;

import lombok.Data;
import ua.com.foxminded.university.model.Lesson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class ScheduleDto {

    private LocalDate date;
    private List<Lesson> lessons = new ArrayList<>();
    private List<ScheduleDto> dtos = new ArrayList<>();

    public List<Lesson> getLessonsByDate(LocalDate date) {
        return this.dtos.stream().filter(day -> day.getDate().equals(date))
                .map(ScheduleDto::getLessons).flatMap(List::stream).collect(toList());
    }

}
