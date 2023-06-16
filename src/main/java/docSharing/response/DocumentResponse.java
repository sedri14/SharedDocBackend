package docSharing.response;

import docSharing.CRDT.CharItem;
import docSharing.entities.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentResponse extends INodeResponse {

    LocalDateTime lastEdited;
    List<CharItemResponse> rawText;

    private DocumentResponse(Long id, String name, String type, LocalDateTime creationDate, LocalDateTime lastEdited, List<CharItemResponse> rawText) {
        super(id, name, type, creationDate);
        this.lastEdited = lastEdited;
        this.rawText = rawText;
    }

    public static DocumentResponse fromDocument(Document doc) {
        List<CharItemResponse> rawTextResponse = doc.getContent().stream().map(CharItemResponse::fromCharItem).collect(Collectors.toList());
        return new DocumentResponse(doc.getId(), doc.getName(), doc.getType().toString(), doc.getCreationDate(), doc.getLastEdited(), rawTextResponse);
    }

    public LocalDateTime getLastEdited() {
        return lastEdited;
    }

    public List<CharItemResponse> getRawText() {
        return rawText;
    }
}
