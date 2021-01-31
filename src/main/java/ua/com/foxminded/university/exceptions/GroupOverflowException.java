package ua.com.foxminded.university.exceptions;

public class GroupOverflowException extends DomainException {

    public GroupOverflowException(String format, Object... args) {
        super(String.format(format, args));
    }

}
