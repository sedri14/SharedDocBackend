package docSharing.documentUserAccess;

import docSharing.entities.Document;
import docSharing.fileSystem.INode;
import docSharing.user.User;
import docSharing.user.UserRole;
import docSharing.exceptions.IllegalOperationException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final AccessRepository accessRepository;

    private static final Logger logger = LogManager.getLogger(AccessService.class.getName());

    /**
     * Changes the role of a user with respect to a document
     *
     * @param document The document for which the user role is to be changed.
     * @param user The user whose role is to be updated.
     * @param userRole The new role (VIEWER/EDITOR)
     * @return The new access entry.
     * @throws IllegalOperationException If an unsupported user role is provided.
     */
    public DocumentUserAccess changeUserRole(Document document, User user, UserRole userRole) {
        Optional<DocumentUserAccess> accessEntry = accessRepository.findByDocumentAndUser(document, user);

        accessEntry.ifPresent(entry -> accessRepository.delete(entry));
        DocumentUserAccess documentUserAccess = switch (userRole) {
            case EDITOR ->
                    DocumentUserAccess.builder()
                            .document(document)
                            .user(user)
                            .role(UserRole.EDITOR)
                            .build();
            case VIEWER ->
                    DocumentUserAccess.builder()
                            .document(document)
                            .user(user)
                            .role(UserRole.VIEWER)
                            .build();
            default -> throw new IllegalOperationException("Unsupported role");
        };

        return accessRepository.save(documentUserAccess);
    }

    /**
     * Retrieves a list of all shared documents accessible to a given user.
     *
     * @param user The user
     * @return A List of INode objects representing the shared documents accessible to the specified user.
     */
    public List<INode> getAllSharedDocumentsWithUser(User user) {
        logger.info("retrieving all shared documents with user {}", user.getEmail());
        List<DocumentUserAccess> accessItems = accessRepository.findByUser(user);

        return accessItems.stream().map(DocumentUserAccess::getDocument).collect(Collectors.toList());
    }

    /**
     * Deletes the role entry associated with a specific user for a given document.
     *
     * @param document The document.
     * @param user The user.
     */
    public void deleteRole(Document document, User user) {
        accessRepository.findByDocumentAndUser(document, user).ifPresent(item -> accessRepository.delete(item));
    }

    /* Helper Methods */

    public List<DocumentUserAccess> getAllUsersWithAccess(Document document) {
        return accessRepository.findByInode(document);
    }

    public UserRole getRole(Document document, User user) {
        return accessRepository.findByDocumentAndUser(document, user).map(DocumentUserAccess::getRole).orElse(UserRole.NON);
    }
}
