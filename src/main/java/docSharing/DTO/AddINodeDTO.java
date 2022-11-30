package docSharing.DTO;

import docSharing.entities.INodeType;

public class AddINodeDTO {

    public Long userId;
    public Long parentId;
    public String name;
    public INodeType type;

    public AddINodeDTO() {

    }

    public AddINodeDTO(Long userId, Long parentId, String name, INodeType type) {
        this.userId = userId;
        this.parentId = parentId;
        this.name = name;
        this.type = type;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(INodeType type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public INodeType getType() {
        return type;
    }
}
