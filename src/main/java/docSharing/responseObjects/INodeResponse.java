package docSharing.responseObjects;

import docSharing.entities.INode;

import java.io.Serializable;
import java.time.LocalDateTime;

public class INodeResponse implements Serializable {

    Long id;
    String name;
    String type;
    LocalDateTime creationDate;

    protected INodeResponse(INode inode) {
        this.id = inode.getId();
        this.name = inode.getName();
        this.type = inode.getType().toString();
        this.creationDate = inode.getCreationDate();
    }

    public static INodeResponse fromINode(INode inode) {
        return new INodeResponse(inode);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
