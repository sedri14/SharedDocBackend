package docSharing.exceptions;

public class InvalidFormatException extends RuntimeException{

    public InvalidFormatException(String param) {
        super("Invalid " + param);
    }
}
