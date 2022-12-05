package docSharing.service;

import docSharing.entities.Document;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.UserRole;
import docSharing.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    PermissionRepository permissionRepository;

    public PermissionService() {
    }

    /**
     * @param user user object
     * @param doc  document object
     * @return optional of permission object
     */
    public Optional<Permission> getPermission(User user, Document doc) {
        return permissionRepository.findByUserAndDocument(user, doc);
    }


    /**
     * @param user user object
     * @param doc  document object
     * @return boolean if the user is editor for that document
     */
    public boolean isEditor(User user, Document doc) {
        return getPermission(user, doc).get().getUserRole() == UserRole.EDITOR;
    }


    /**
     * @param user user object
     * @param doc  document object
     * @return boolean if the user is just a viewer for that document
     */
    public boolean isJustViewer(User user, Document doc) {
        return getPermission(user, doc).get().getUserRole() == UserRole.VIEWER;
    }


    /**
     * @param p permission object
     */
    public void setPermission(Permission p) {
        permissionRepository.save(p);
    }

    /**
     * @param doc      document object
     * @param user     user object
     * @param userRole user role to update to
     * @return permission object
     */
    public Permission updatePermission(Document doc, User user, UserRole userRole) {
        Permission permission = permissionRepository.findByUserAndDocument(user, doc).get();
        permission.setUserRole(userRole);
        return permissionRepository.save(permission);
    }


    /**
     * @param doc      document object
     * @param user     user object
     * @param userRole user role to update to
     * @return permission object
     */
    public Permission addPermission(Document doc, User user, UserRole userRole) {
        Permission p = new Permission(user, doc, userRole);
        return permissionRepository.save(p);
    }

    /**
     * @param doc  document object
     * @param user user object
     * @return permission object
     */
    public Permission delete(Document doc, User user) {
        Permission p = permissionRepository.findByUserAndDocument(user, doc).get();
        permissionRepository.delete(p);
        return p;
    }

    /**
     * @param doc  document object
     * @param user user object
     * @return boolean if the user has any permission for that document
     */
    public boolean isExist(Document doc, User user) {
        return permissionRepository.existsByUserAndDocument(user, doc);
    }


    /**
     * @param doc      document object
     * @param user     user object
     * @param userRole user role to change to
     * @param isDelete is the operation delete the permission
     * @return the new user role
     */
    public UserRole changeRole(Document doc, User user, UserRole userRole, boolean isDelete) {

        Permission modified;
        if (isDelete && isExist(doc, user)) {
            delete(doc, user);
            return UserRole.NON;
        } else {
            if (isExist(doc, user)) {
                modified = updatePermission(doc, user, userRole);
            } else {
                modified = addPermission(doc, user, userRole);
            }
        }

        return modified.getUserRole();
    }
}


