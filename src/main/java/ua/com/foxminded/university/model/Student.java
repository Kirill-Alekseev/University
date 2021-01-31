package ua.com.foxminded.university.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import ua.com.foxminded.university.service.validation.annotation.BirthDate;
import ua.com.foxminded.university.service.validation.annotation.PersonName;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

import static javax.persistence.CascadeType.REFRESH;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "group", callSuper = false)
@ToString(exclude = "group", callSuper = true)
@Entity
@Table(name = "students")
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "first_name")
    @PersonName
    private String firstName;
    @Column(name = "last_name")
    @PersonName
    private String lastName;
    @ManyToOne(optional = false, cascade = REFRESH)
    @JoinColumn(name = "group_id")
    @NotNull(message = "{notnull}")
    @JsonIgnoreProperties("students")
    private Group group;
    @NotNull(message = "{notnull}")
    @Email(regexp = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$",
            message = "{email.pattern}")
    private String email;
    @Column(name = "phone_number")
    @NotNull(message = "{notnull}")
    @Pattern(regexp = "^(\\s*)?(\\+)?([- _():=+]?\\d[- _():=+]?){10,14}(\\s*)?$",
            message = "{phoneNumber.pattern}")
    private String phoneNumber;
    @Column(name = "birth_date")
    @BirthDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthDate;

    public Student(Integer id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
