package docSharing.response;

import docSharing.entities.Document;
import docSharing.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentWithUserRoleResponse extends INodeResponse {

    LocalDateTime lastEdited;

    UserRole userRole;
    List<CharItemResponse> rawText;

    private DocumentWithUserRoleResponse(Long id, String name, String type, LocalDateTime creationDate, LocalDateTime lastEdited, List<CharItemResponse> rawText, UserRole userRole) {
        super(id, name, type, creationDate);
        this.lastEdited = lastEdited;
        this.rawText = rawText;
        this.userRole = userRole;
    }

    public static DocumentWithUserRoleResponse fromDocument(Document doc, UserRole userRole) {
        List<CharItemResponse> rawTextResponse = doc.getContent().stream().map(CharItemResponse::fromCharItem).collect(Collectors.toList());
        return new DocumentWithUserRoleResponse(doc.getId(), doc.getName(), doc.getType().toString(), doc.getCreationDate(), doc.getLastEdited(), rawTextResponse, userRole);
    }

    public LocalDateTime getLastEdited() {
        return lastEdited;
    }

    public List<CharItemResponse> getRawText() {
        return rawText;
    }

    public UserRole getUserRole() {
        return userRole;
    }
}
