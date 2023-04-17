package docSharing.DTO;

import docSharing.enums.UserRole;

public class ChangeRoleDTO {
    public String email;
    public UserRole userRole;
    public boolean isDeleteRole;

    public ChangeRoleDTO() {
    }

    public ChangeRoleDTO(String email, UserRole userRole, boolean isDeleteRole) {
        this.email = email;
        this.userRole = userRole;
        this.isDeleteRole = isDeleteRole;
    }
}
