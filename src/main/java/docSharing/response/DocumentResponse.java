package docSharing.response;

import docSharing.CRDT.PositionedChar;

import java.time.LocalDateTime;
import java.util.List;

public class DocumentResponse {

    Long id;

    String name;

    LocalDateTime creationDate;

    LocalDateTime lastEdited;

    List<PositionedChar> rawText;

    DocumentResponse() {

    }

    public DocumentResponse(Long id, String name, LocalDateTime creationDate, LocalDateTime lastEdited, List<PositionedChar> rawText) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.lastEdited = lastEdited;
        this.rawText = rawText;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getLastEdited() {
        return lastEdited;
    }

    public List<PositionedChar> getRawText() {
        return rawText;
    }
}
