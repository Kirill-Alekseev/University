package ua.com.foxminded.university.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WebNotFoundException extends RuntimeException {

    public WebNotFoundException(String format, Throwable cause, Object... args) {
        super(String.format(format, args), cause);
    }

    public WebNotFoundException(String format, Object... args) {
        super(String.format(format, args));
    }
}
