package ua.com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static javax.persistence.CascadeType.REFRESH;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "cathedra", callSuper = false)
@ToString(exclude = "cathedra", callSuper = true)
@Entity
@Table(name = "subjects")
public class Subject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Pattern(regexp = "[A-Z][a-z\\s]{2,30}", message = "{name.pattern}")
    private String name;
    @ManyToOne(optional = false, cascade = REFRESH)
    @JoinColumn(name = "cathedra_id")
    @NotNull(message = "{notnull}")
    @JsonIgnoreProperties("subjects")
    private Cathedra cathedra;

    public Subject(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Subject(String name) {
        this.name = name;
    }

    public Subject(Integer id) {
        this.id = id;
    }
}
