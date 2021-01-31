package ua.com.foxminded.university.service.validation.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "[A-Z][a-zA-Z]{1,19}")
@ReportAsSingleViolation
public @interface PersonName {

    String message() default "{person.name.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
