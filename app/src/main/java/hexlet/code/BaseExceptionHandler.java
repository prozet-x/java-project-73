package hexlet.code;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;
import java.util.NoSuchElementException;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ControllerAdvice
@ResponseBody
public class BaseExceptionHandler {
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ObjectError> validationExceptionHandler(MethodArgumentNotValidException ex) {
        return ex.getAllErrors();
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String validationExceptionHandler(NoSuchElementException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(UsernameNotFoundException.class)
    public String usernameNotFoundExceptionHandler(UsernameNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String dataIntegrityViolationExceptionHandler(DataIntegrityViolationException ex) {
        return ex.getMessage();
    }
}
