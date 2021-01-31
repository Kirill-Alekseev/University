package ua.com.foxminded.university.service.validation.annotation;

import ua.com.foxminded.university.service.validation.LessonTimeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LessonTimeValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LessonTimeConstraint {

    String message() default "{lessonTime.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
