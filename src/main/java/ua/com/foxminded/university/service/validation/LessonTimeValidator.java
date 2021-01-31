package ua.com.foxminded.university.service.validation;

import ua.com.foxminded.university.model.LessonTime;
import ua.com.foxminded.university.service.validation.annotation.LessonTimeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LessonTimeValidator implements ConstraintValidator<LessonTimeConstraint,
        LessonTime> {

    @Override
    public void initialize(LessonTimeConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(LessonTime value, ConstraintValidatorContext context) {
        if (value.getBeginTime() == null || value.getEndTime() == null) {
            return true;
        }
        return value.getBeginTime().isBefore(value.getEndTime());
    }
}
