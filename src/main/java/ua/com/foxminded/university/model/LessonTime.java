package ua.com.foxminded.university.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import ua.com.foxminded.university.service.validation.annotation.LessonTimeConstraint;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "lesson_time")
@LessonTimeConstraint
public class LessonTime extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @DateTimeFormat(iso = ISO.TIME)
    @Column(name = "begin_time")
    @NotNull(message = "{notnull}")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime beginTime;
    @DateTimeFormat(iso = ISO.TIME)
    @Column(name = "end_time")
    @NotNull(message = "{notnull}")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime endTime;

    public LessonTime(Integer id, LocalTime beginTime, LocalTime endTime) {
        this.id = id;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public LessonTime(LocalTime beginTime, LocalTime endTime) {
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public LessonTime(Integer id) {
        this.id = id;
    }
}

