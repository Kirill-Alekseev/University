package ua.com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.REFRESH;

@Data
@NoArgsConstructor
@ToString(of = {"id", "name"}, callSuper = true)
@EqualsAndHashCode(of = {"id", "name"}, callSuper = false)
@Entity
@Table(name = "faculties")
public class Faculty extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Pattern(regexp = "[A-Z][a-zA-Z\\s]{3,50}", message = "{name.pattern}")
    private String name;
    @OneToMany(mappedBy = "faculty", fetch = FetchType.EAGER, cascade = REFRESH)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnoreProperties("faculty")
    private List<Cathedra> cathedras = new ArrayList<>();
    @OneToMany(mappedBy = "faculty", fetch = FetchType.EAGER, cascade = REFRESH)
    @Fetch(FetchMode.SUBSELECT)
    @JsonIgnoreProperties("faculty")
    private List<Specialty> specialties = new ArrayList<>();

    public Faculty(Integer id) {
        this.id = id;
    }
}
