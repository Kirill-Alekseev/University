package ua.com.foxminded.university.service.validation.annotation;

import ua.com.foxminded.university.service.validation.BirthDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BirthDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BirthDate {

    String maximumAge() default "1980-01-01";

    int minimumAge() default 16;

    String message() default "{birthday}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
