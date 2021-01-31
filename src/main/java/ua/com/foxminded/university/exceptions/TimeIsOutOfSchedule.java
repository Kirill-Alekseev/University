package ua.com.foxminded.university.exceptions;

public class TimeIsOutOfSchedule extends DomainException {

    public TimeIsOutOfSchedule(String format, Object... args) {
        super(String.format(format, args));
    }

}
