package docSharing.controllers;

import com.ibm.icu.util.ICUCloneNotSupportedException;
import docSharing.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({INodeNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex) {
        return ApiError.newResponseApiError(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalOperationException.class, INodeNameExistsException.class, MissingControllerParameterException.class, InvalidFormatException.class, InvalidPasswordException.class})
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex) {
        return ApiError.newResponseApiError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
