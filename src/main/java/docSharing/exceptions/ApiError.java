package docSharing.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiError {

    private String message;
    private LocalDateTime timeStamp;


    public static ResponseEntity<ApiError> newResponseApiError(String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(new ApiError(message, LocalDateTime.now()), httpStatus);
    }
}
