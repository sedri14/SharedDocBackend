package docSharing.documentUserAccess;

import docSharing.entities.BaseEntity;
import docSharing.entities.Document;
import docSharing.user.UserRole;
import docSharing.user.User;
import lombok.*;
import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shared")
public class DocumentUserAccess extends BaseEntity implements Serializable {
    @ManyToOne
    @JoinColumn(name = "inode_id")
    private Document document;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
