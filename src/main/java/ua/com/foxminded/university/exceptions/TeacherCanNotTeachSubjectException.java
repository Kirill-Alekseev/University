package ua.com.foxminded.university.exceptions;

public class TeacherCanNotTeachSubjectException extends DomainException {

    public TeacherCanNotTeachSubjectException(String format, Object... args) {
        super(String.format(format, args));
    }

}
