package docSharing.controllers;

import docSharing.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({INodeNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex) {
        return ApiError.newResponseApiError(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalOperationException.class, INodeNameExistsException.class, MissingControllerParameterException.class, InvalidFormatException.class, IncorrectPasswordException.class, BadCredentialsException.class})
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException ex) {
        return ApiError.newResponseApiError(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
