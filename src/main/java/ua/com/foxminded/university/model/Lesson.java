package ua.com.foxminded.university.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@ToString(of = {"id", "date"}, callSuper = true)
@EqualsAndHashCode(of = {"id", "date"}, callSuper = false)
@Entity
@Table(name = "lessons")
public class Lesson extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    @NotNull(message = "{notnull}")
    private Subject subject;
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    @NotNull(message = "{notnull}")
    private Classroom classroom;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @NotNull(message = "{notnull}")
    private Teacher teacher;
    @ManyToOne
    @JoinColumn(name = "group_id")
    @NotNull(message = "{notnull}")
    private Group group;
    @Column(name = "lesson_date")
    @DateTimeFormat(iso = ISO.DATE)
    @NotNull(message = "{notnull}")
    @FutureOrPresent(message = "{date.futureOrPresent}")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "lesson_time_id")
    @NotNull(message = "{notnull}")
    private LessonTime lessonTime;

}
