package docSharing.service;

import docSharing.entities.INode;
import docSharing.entities.SharedRole;
import docSharing.entities.User;
import docSharing.enums.UserRole;
import docSharing.exceptions.IllegalOperationException;
import docSharing.repository.SharedRoleRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SharedRoleService {

    @Autowired
    private SharedRoleRepository sharedRoleRepository;

    private static Logger logger = LogManager.getLogger(SharedRoleService.class.getName());

    //Add or change the user role
    public SharedRole changeUserRole(INode inode, User user, UserRole userRole) {
        Optional<SharedRole> entry = sharedRoleRepository.findByInodeAndUser(inode, user);
        SharedRole sharedRole = null;

        entry.ifPresent(role -> sharedRoleRepository.delete(role));
        switch (userRole) {
            case EDITOR:
                //Add as editor
                sharedRole = SharedRole.createSharedEditor(inode, user);
                break;
            case VIEWER:
                //Add as viewer
                sharedRole = SharedRole.createSharedViewer(inode, user);
                break;
            default:
                throw new IllegalOperationException("Unsupported role");
        }

        return sharedRoleRepository.save(sharedRole);
    }

    public List<INode> getAllSharedFilesWithUser(User user) {
        //return all inodes shared with this user.
        logger.info("Retrieving all shared documents with user {}", user.getEmail());
        List<SharedRole> sharedWithUser = sharedRoleRepository.findByUser(user);

        return sharedWithUser.stream().map(SharedRole::getInode).collect(Collectors.toList());
    }

    public List<SharedRole> getAllUsersWithPermission(INode inode) {
        //return all users that have editor or viewer permissions to this inode.
        List<SharedRole> usersEnrolled = sharedRoleRepository.findByInode(inode);

        return usersEnrolled;
    }

    public SharedRole deleteRole(INode inode, User user) {
        Optional<SharedRole> entry = sharedRoleRepository.findByInodeAndUser(inode, user);
        if (entry.isPresent()) {
            sharedRoleRepository.delete(entry.get());
        } else {
            throw new IllegalOperationException("Role doesn't exist for this inode");
        }

        return SharedRole.createSharedRole(inode, user, UserRole.NON);
    }

    public boolean hasRole(INode inode, User user) {
        return sharedRoleRepository.findByInodeAndUser(inode, user).isPresent();
    }
}
