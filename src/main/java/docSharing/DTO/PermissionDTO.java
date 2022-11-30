package docSharing.DTO;

import docSharing.entities.UserRole;

public class PermissionDTO {

    public Long userId;

    public Long docId;

    public UserRole userRole;

    public PermissionDTO() {
    }

    public PermissionDTO(Long userId, Long docId, UserRole userRole) {
        this.userId = userId;
        this.docId = docId;
        this.userRole = userRole;
    }
}