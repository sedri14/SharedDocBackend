package docSharing.response;

import docSharing.CRDT.Identifier;
import docSharing.CRDT.PositionedChar;
import docSharing.entities.Document;
import docSharing.entities.INode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentResponse extends INodeResponse {

    LocalDateTime lastEdited;
    List<PositionedCharResponse> rawText;

    private DocumentResponse(Long id, String name, String type, LocalDateTime creationDate, LocalDateTime lastEdited, List<PositionedCharResponse> rawText) {
        super(id, name, type, creationDate);
        this.lastEdited = lastEdited;
        this.rawText = rawText;
    }

    public static DocumentResponse fromDocument(Document doc, List<PositionedChar> rawText) {
        List<PositionedCharResponse> rawTextResponse = rawText.stream().map(DocumentResponse::convertPositionedCharToResponse).collect(Collectors.toList());
        return new DocumentResponse(doc.getId(), doc.getName(), doc.getType().toString(), doc.getCreationDate(), doc.getLastEdited(), rawTextResponse);
    }

    public LocalDateTime getLastEdited() {
        return lastEdited;
    }

    public List<PositionedCharResponse> getRawText() {
        return rawText;
    }

    private static PositionedCharResponse convertPositionedCharToResponse(PositionedChar positionedChar) {
        Character val = positionedChar.getValue();
        List<Integer> pos = positionedChar.getPosition().stream()
                .map(Identifier::getDigit)
                .collect(Collectors.toList());

        return new PositionedCharResponse(val, pos);
    }
}
