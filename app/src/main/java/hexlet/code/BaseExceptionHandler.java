
package hexlet.code;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

@ControllerAdvice
@ResponseBody
public class BaseExceptionHandler {
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ObjectError> validationExceptionHandler(MethodArgumentNotValidException ex) {
        return ex.getAllErrors();
    }
}
