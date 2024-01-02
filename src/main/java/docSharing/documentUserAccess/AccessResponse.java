package docSharing.documentUserAccess;

import docSharing.user.UserRole;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessResponse {

    private String email;
    private String name;
    private UserRole role;
}
