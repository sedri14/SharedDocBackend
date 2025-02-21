package docSharing.fileSystem;

import docSharing.entities.Document;
import docSharing.requestObjects.responseObjects.CharItemResponse;
import docSharing.user.UserRole;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DocumentWithUserRoleResponse extends INodeResponse {

    LocalDateTime lastEdited;
    UserRole userRole;
    List<CharItemResponse> rawText;

    private DocumentWithUserRoleResponse(Document doc, UserRole userRole) {
        super(doc);
        this.lastEdited = doc.getLastEdited();
        this.rawText = doc.getContent().stream().map(CharItemResponse::fromCharItem).collect(Collectors.toList());
        this.userRole = userRole;
    }

    public static DocumentWithUserRoleResponse fromDocument(Document doc, UserRole userRole) {
        return new DocumentWithUserRoleResponse(doc, userRole);
    }
}
