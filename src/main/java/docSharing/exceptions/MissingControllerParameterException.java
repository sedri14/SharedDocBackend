package docSharing.exceptions;

public class MissingControllerParameterException extends RuntimeException{
    public MissingControllerParameterException(String message) {
        super(message + " parameter is missing");
    }
}
