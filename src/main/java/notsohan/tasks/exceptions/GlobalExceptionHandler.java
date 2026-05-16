package notsohan.tasks.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException e){
        ErrorResponse errorResponse =
                new ErrorResponse(LocalDateTime.now(),
                        e.getMessage(),
                        "Task Not Found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /*
    * There's a @RestControllerAdvice annotation too, which is the same
    * as @ControllerAdvice but automatically handles the @ResponseBody as well.
    * Since we're already returning a ResponseEntity, we aren't required to
    * use @RestControllerAdvice for now. However, if we weren't returning a
    * ResponseEntity and wanted to keep the @ControllerAdvice annotation, we
    * could simply add @ResponseBody alongside the @ExceptionHandler annotation.
    * */
}
