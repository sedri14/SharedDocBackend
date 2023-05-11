package docSharing.response;

import docSharing.entities.INode;

import java.io.Serializable;
import java.time.LocalDateTime;

public class INodeResponse implements Serializable {

    Long id;
    String name;
    String type;
    LocalDateTime creationDate;
    protected INodeResponse(Long id, String name, String type, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.creationDate = creationDate;
    }

    public static INodeResponse fromINode(INode inode) {
        return new INodeResponse(inode.getId(), inode.getName(), inode.getType().toString(), inode.getCreationDate());
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
