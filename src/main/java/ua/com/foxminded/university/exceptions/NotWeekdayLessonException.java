package ua.com.foxminded.university.exceptions;

public class NotWeekdayLessonException extends DomainException {

    public NotWeekdayLessonException(String format, Object... args) {
        super(String.format(format, args));
    }

}
