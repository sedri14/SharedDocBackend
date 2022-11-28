package docSharing.DTO;

import docSharing.test.UpdateType;

public class ReturnDocumentMessage {
    public String userName;
    public String documentText;
    public int startPosition;
    public int endPosition;
    public UpdateType updateType;

    public ReturnDocumentMessage(String userName, String documentText, int startPosition, int endPosition, UpdateType updateType) {
        this.userName = userName;
        this.documentText = documentText;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.updateType = updateType;

    }
}
