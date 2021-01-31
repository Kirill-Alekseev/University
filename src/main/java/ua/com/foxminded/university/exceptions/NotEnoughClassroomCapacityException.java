package ua.com.foxminded.university.exceptions;

public class NotEnoughClassroomCapacityException extends DomainException {

    public NotEnoughClassroomCapacityException(String format, Object... args) {
        super(String.format(format, args));
    }

}
