package ua.com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ua.com.foxminded.university.service.validation.annotation.PersonName;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.REFRESH;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"cathedras", "subjects"}, callSuper = false)
@ToString(exclude = {"cathedras", "subjects"}, callSuper = true)
@Entity
@Table(name = "teachers")
public class Teacher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @PersonName
    @Column(name = "first_name")
    private String firstName;
    @PersonName
    @Column(name = "last_name")
    private String lastName;
    @ManyToMany(fetch = FetchType.EAGER, cascade = REFRESH)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "cathedras_teachers",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "cathedra_id"))
    @JsonIgnoreProperties({"teachers", "subjects"})
    private List<Cathedra> cathedras = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER, cascade = REFRESH)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "teachers_subjects",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    @JsonIgnoreProperties("cathedra")
    private List<Subject> subjects = new ArrayList<>();

    public Teacher(Integer id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Teacher(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Teacher(Integer id) {
        this.id = id;
    }
}
