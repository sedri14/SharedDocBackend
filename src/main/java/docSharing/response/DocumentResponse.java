package docSharing.response;

import docSharing.CRDT.Char;
import docSharing.entities.Document;
import java.time.LocalDateTime;
import java.util.List;

public class DocumentResponse extends INodeResponse {

    LocalDateTime lastEdited;
    List<Char> rawText;

    private DocumentResponse(Long id, String name, String type, LocalDateTime creationDate, LocalDateTime lastEdited, List<Char> rawText) {
        super(id, name, type, creationDate);
        this.lastEdited = lastEdited;
        this.rawText = rawText;
    }

    public static DocumentResponse fromDocument(Document doc, List<Char> rawText) {
        return new DocumentResponse(doc.getId(), doc.getName(), doc.getType().toString(), doc.getCreationDate(), doc.getLastEdited(), rawText);
    }

    public LocalDateTime getLastEdited() {
        return lastEdited;
    }

    public List<Char> getRawText() {
        return rawText;
    }
}
