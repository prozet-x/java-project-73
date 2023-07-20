
package hexlet.code;

import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@ResponseBody
public class BaseExceptionHandler {
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ObjectError> validationExceptionHandler(MethodArgumentNotValidException ex) {
        return ex.getAllErrors();
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler({NoSuchUserException.class, NoSuchElementException.class})
    public String validationExceptionHandler(Exception ex) {
        return ex.getMessage();
    }
}
