package docSharing.response;

import docSharing.enums.UserRole;

public class PermissionResponse {

    UserRole userRole;

    public PermissionResponse(UserRole userRole) {
        this.userRole = userRole;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
