package docSharing.response;

import docSharing.entities.SharedRole;
import docSharing.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SharedRoleResponse {
    String email;
    String name;
    UserRole role;

    public static SharedRoleResponse fromSharedRole(SharedRole sharedRole) {
        return new SharedRoleResponse(sharedRole.getUser().getEmail(), sharedRole.getUser().getName(), sharedRole.getRole());
    }
}
