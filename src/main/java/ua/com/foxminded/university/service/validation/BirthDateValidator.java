package ua.com.foxminded.university.service.validation;


import ua.com.foxminded.university.service.validation.annotation.BirthDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BirthDateValidator implements ConstraintValidator<BirthDate, LocalDate> {

    private int minimumAge;

    private String maximumAge;

    @Override
    public void initialize(BirthDate constraintAnnotation) {
        this.minimumAge = constraintAnnotation.minimumAge();
        this.maximumAge = constraintAnnotation.maximumAge();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null || value.isBefore(LocalDate.parse(maximumAge))) {
            return false;
        }
        return LocalDate.now().minusYears(minimumAge).isAfter(value);
    }
}
