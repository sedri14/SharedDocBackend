package docSharing.exceptions;

public class IncorrectPasswordException extends RuntimeException{

    public IncorrectPasswordException() {
        super("Incorrect password");
    }
}
