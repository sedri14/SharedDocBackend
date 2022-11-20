package docSharing.DTO;

public class ReturnDocumentMessage {
    public String userName;
    public String documentText;

    public ReturnDocumentMessage(String userName, String documentText) {
        this.userName = userName;
        this.documentText = documentText;
    }
}
