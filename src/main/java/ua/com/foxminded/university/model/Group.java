package ua.com.foxminded.university.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.REFRESH;

@Data
@NoArgsConstructor
@ToString(exclude = {"specialty", "students"}, callSuper = true)
@EqualsAndHashCode(exclude = {"specialty", "students"}, callSuper = false)
@Entity
@Table(name = "groups")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Pattern(regexp = "^[A-Z]{2}-[0-9]{2}", message = "{group.name.pattern}")
    private String name;
    @ManyToOne(optional = false, cascade = REFRESH)
    @JoinColumn(name = "specialty_id")
    @NotNull(message = "{notnull}")
    @JsonIgnoreProperties("groups")
    private Specialty specialty;
    @Range(min = 1, max = 6, message = "{course.range}")
    private int course;
    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = REFRESH)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnoreProperties("group")
    private List<Student> students = new ArrayList<>();

    public Group(Integer id, String name, int course) {
        this.id = id;
        this.name = name;
        this.course = course;
    }

    public Group(String name) {
        this.name = name;
    }

    public Group(Integer id) {
        this.id = id;
    }
}
