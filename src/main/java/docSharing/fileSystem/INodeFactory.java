package docSharing.fileSystem;

import docSharing.entities.Document;
import docSharing.enums.INodeType;
import docSharing.user.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class INodeFactory {

    private INodeFactory() {
    }

    static Document createNewEmptyDocument(String name, INode parent, User owner) {
        return new Document(name,
                INodeType.FILE,
                LocalDateTime.now(),
                parent,
                owner,
                new ArrayList<>(),
                LocalDateTime.now());
    }

    static INode createNewDirectory(String name, INode parent, User owner) {
        return new INode(name,
                INodeType.DIR,
                LocalDateTime.now(),
                owner,
                new HashMap<>(),
                parent,
                null);
    }

    public static INode createRootDirectoryForUser(User user) {
        return new INode("root_" + user.getEmail(),
                INodeType.DIR,
                LocalDateTime.now(),
                user,
                new HashMap<>(),
                null,
                null);
    }
}
