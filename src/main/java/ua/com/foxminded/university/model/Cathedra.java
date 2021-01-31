package ua.com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.REFRESH;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
@ToString(of = {"id", "name"}, callSuper = true)
@Entity
@Table(name = "cathedras")
public class Cathedra extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Pattern(regexp = "[A-Z][A-Za-z\\s]{1,50}", message = "{name.pattern}")
    private String name;
    @ManyToOne(optional = false, cascade = REFRESH)
    @JoinColumn(name = "faculty_id")
    @NotNull(message = "{notnull}")
    @JsonIgnoreProperties("cathedras")
    private Faculty faculty;
    @ManyToMany(fetch = FetchType.EAGER, cascade = REFRESH)
    @JoinTable(name = "cathedras_teachers",
            joinColumns = @JoinColumn(name = "cathedra_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id"))
    @JsonIgnoreProperties({"cathedras", "subjects"})
    private List<Teacher> teachers = new ArrayList<>();
    @OneToMany(mappedBy = "cathedra", fetch = FetchType.EAGER, cascade = REFRESH)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnoreProperties("cathedra")
    private List<Subject> subjects = new ArrayList<>();

    public Cathedra(String name) {
        this.name = name;
    }

    public Cathedra(Integer id, String name, Faculty faculty) {
        this.id = id;
        this.name = name;
        this.faculty = faculty;
    }
}
