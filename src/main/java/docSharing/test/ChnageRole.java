package docSharing.test;

import docSharing.entities.UserRole;

public class ChnageRole {
    public Long ownerId;
    public String email;
    public UserRole userRole;

    public ChnageRole(Long ownerId, String email, UserRole userRole) {
        this.ownerId = ownerId;
        this.email = email;
        this.userRole = userRole;
    }
}
