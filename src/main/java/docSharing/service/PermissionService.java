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

//    @Autowired
//    DocService docService;

    public PermissionService() {
    }


    public Optional<Permission> getPermission(User user, Document doc) {
        return permissionRepository.findByUserAndDocument(user, doc);
    }

    public boolean isEditor(User user, Document doc) {
        return getPermission(user, doc).get().getUserRole() == UserRole.EDITOR;
    }

    public boolean isJustViewer(User user, Document doc) {
        return getPermission(user, doc).get().getUserRole() == UserRole.VIEWER;
    }

    public void setPermission(Permission p) {
        permissionRepository.save(p);
    }

    public Permission updatePermission(Document doc, User user, UserRole userRole) {
        Permission permission = permissionRepository.findByUserAndDocument(user, doc).get();
        permission.setUserRole(userRole);
        return permissionRepository.save(permission);
    }

    public Permission addPermission(Document doc, User user, UserRole userRole) {
        Permission p = new Permission(user, doc, userRole);
        return permissionRepository.save(p);
    }

    public Permission delete(Document doc, User user) {
        Permission p = permissionRepository.findByUserAndDocument(user, doc).get();
        permissionRepository.delete(p);
        return p;
    }

    public boolean isExist(Document doc, User user) {
        return permissionRepository.existsByUserAndDocument(user, doc);
    }

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


