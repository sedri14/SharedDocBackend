package docSharing.entities;

import docSharing.enums.UserRole;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "shared")
@Getter
public class SharedRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inode_id")
    private INode inode;

    @ManyToOne
    @JoinColumn(name = "shared_with_user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    SharedRole() {

    }

    private SharedRole(INode inode, User user, UserRole role) {
        this.inode = inode;
        this.user = user;
        this.role = role;
    }

    public static SharedRole createSharedEditor(INode inode, User user) {
        return new SharedRole(inode, user, UserRole.EDITOR);
    }

    public static SharedRole createSharedViewer(INode inode, User user) {
        return new SharedRole(inode, user, UserRole.VIEWER);
    }

    public static SharedRole createSharedRole(INode inode, User user, UserRole userRole) {
        return new SharedRole(inode, user, userRole);
    }
}
