package phi.raiz.tech.todolist.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException message){

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message.getMostSpecificCause().getMessage());
    }
}
