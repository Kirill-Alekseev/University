package ua.com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@Table(name = "specialties")
public class Specialty extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Pattern(regexp = "[A-Za-z\\s]{3,50}", message = "{name.pattern}")
    private String name;
    @ManyToOne(optional = false, cascade = REFRESH)
    @JoinColumn(name = "faculty_id")
    @NotNull(message = "{notnull}")
    @JsonIgnoreProperties("specialties")
    private Faculty faculty;
    @OneToMany(mappedBy = "specialty", fetch = FetchType.EAGER, cascade = REFRESH)
    @JsonIgnoreProperties("specialty")
    private List<Group> groups = new ArrayList<>();

    public Specialty(String name) {
        this.name = name;
    }

}
