package docSharing.documentUserAccess;

import docSharing.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessRequest {

    private String email;
    private UserRole userRole;
    private boolean isDeleteRole;

}
