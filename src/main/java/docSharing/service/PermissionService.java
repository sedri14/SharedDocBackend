package docSharing.service;

import docSharing.entities.Document;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.UserRole;
import docSharing.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    @Autowired
    PermissionRepository permissionRepository;

//    @Autowired
//    DocService docService;

    public PermissionService() {
    }


    public Permission getPermission(User user, Document doc) {
        return permissionRepository.findByUserAndDocument(user, doc);
    }

    public boolean isEditor(User user, Document doc) {
        return getPermission(user, doc).getUserRole() == UserRole.EDITOR;
    }

    public boolean isJustViewer(User user, Document doc) {
        return getPermission(user, doc).getUserRole() == UserRole.VIEWER;
    }

    public void setPermission(Permission p) {
        permissionRepository.save(p);
    }

    public void updatePermission(Document doc, User user, UserRole userRole) {
        Permission permission = permissionRepository.findByUserAndDocument(user, doc);
        permission.setUserRole(userRole);
        permissionRepository.save(permission);
    }

    public boolean addPermission(Document doc, User user, UserRole userRole) {
        Permission p = new Permission(user,doc,userRole);
        permissionRepository.save(p);
        return true;
    }

    public boolean delete(Document doc, User user) {
        Permission p = permissionRepository.findByUserAndDocument(user, doc);
        permissionRepository.delete(p);
        return true;
    }

    public boolean isExist(Document doc, User user) {
        return permissionRepository.existsByUserAndDocument(user,doc);
    }
}


