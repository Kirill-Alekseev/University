package ua.com.foxminded.university.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Entity
@Table(name = "classrooms")
public class Classroom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull(message = "{notnull}")
    @Pattern(regexp = "[0-9]{3}", message = "{name.pattern}")
    private String name;
    @Range(min = 5, max = 100, message = "{capacity.range}")
    private int capacity;

    public Classroom(Integer id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public Classroom(String name) {
        this.name = name;
    }

    public Classroom(Integer id) {
        this.id = id;
    }
}
