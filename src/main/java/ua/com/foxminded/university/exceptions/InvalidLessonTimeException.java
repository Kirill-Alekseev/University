package ua.com.foxminded.university.exceptions;

public class InvalidLessonTimeException extends DomainException {

    public InvalidLessonTimeException(String format, Object... args) {
        super(String.format(format, args));
    }

}
