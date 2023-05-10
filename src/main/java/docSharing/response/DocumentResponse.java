package docSharing.response;

import docSharing.CRDT.PositionedChar;
import docSharing.entities.Document;
import docSharing.entities.INode;

import java.time.LocalDateTime;
import java.util.List;

public class DocumentResponse extends INodeResponse {

    LocalDateTime lastEdited;
    List<PositionedChar> rawText;

    public DocumentResponse(Long id, String name, String type, LocalDateTime creationDate, LocalDateTime lastEdited, List<PositionedChar> rawText) {
        super(id, name, type, creationDate);
        this.lastEdited = lastEdited;
        this.rawText = rawText;
    }

    public static DocumentResponse fromDocument(Document doc, List<PositionedChar> rawText) {
        return new DocumentResponse(doc.getId(), doc.getName(), doc.getType().toString(), doc.getCreationDate(), doc.getLastEdited(), rawText);
    }

    public LocalDateTime getLastEdited() {
        return lastEdited;
    }

    public List<PositionedChar> getRawText() {
        return rawText;
    }
}
