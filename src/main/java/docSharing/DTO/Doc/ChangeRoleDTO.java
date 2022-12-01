package docSharing.DTO.Doc;

import docSharing.entities.UserRole;

public class ChangeRoleDTO {
    public Long ownerId;
    public String email;
    public UserRole userRole;
    public boolean isDelete;

    public ChangeRoleDTO() {
    }

    public ChangeRoleDTO(Long ownerId, String email, UserRole userRole) {
        this.ownerId = ownerId;
        this.email = email;
        this.userRole = userRole;
    }
}
