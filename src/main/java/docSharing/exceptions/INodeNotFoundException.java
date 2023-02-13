package docSharing.exceptions;

public class INodeNotFoundException extends RuntimeException{

    //TODO: add an informative message
    public INodeNotFoundException(String message) {
        super(message);
    }
}
