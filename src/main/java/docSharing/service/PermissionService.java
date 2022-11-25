package docSharing.service;

import docSharing.entities.Document;
import docSharing.entities.Permission;
import docSharing.entities.User;
import docSharing.entities.UserRole;
import docSharing.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    PermissionRepository permissionRepository;

    public PermissionService() {
    }

    public boolean isEditor(Long docId, Long userId) {
//        UserRole userRole = permissionRepository.findByDocIdAndUserId(docId, userId);

//        Permission permission = permissionRepository.findPermissionByDocIdAndUserId(docId, userId);
//        permissionRepository.findBydoc_id(docId);
//        System.out.println(permission);
//        return permission.getUserRole() == UserRole.EDITOR;
        return false;
    }

    //
//    public boolean checkUserPermission(Long userId, Long docId) {
//    }
    public boolean changeUserRollInDoc(Long docId, Long userId, UserRole userRole) {

//        Permission permission = permissionRepository.findByDocIdAndUserId(docId, userId);
//        permission.setUserRole(userRole);
//        permissionRepository.save( permission);

        return true;
    }

    public boolean checkIfOwner(Long ownerId) {
        return true;
    }
    public boolean addPermission(Document doc , User user){
        return false;
    }


}
