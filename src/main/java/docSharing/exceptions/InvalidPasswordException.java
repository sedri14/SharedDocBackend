package docSharing.exceptions;

public class InvalidPasswordException extends RuntimeException{

    public InvalidPasswordException(String email) {
        super("invalid password for user:  " + email);
    }
}
