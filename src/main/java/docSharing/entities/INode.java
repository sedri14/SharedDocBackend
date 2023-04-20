package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import docSharing.enums.INodeType;
import docSharing.enums.UserRole;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "inodes")
@Inheritance(strategy = InheritanceType.JOINED)
public class INode implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    @Column(name = "name", nullable = false)
    protected String name;
    @Enumerated(EnumType.STRING)
    protected INodeType type;
    @Column(name = "creation_date")
    protected LocalDateTime creationDate;

    @JsonIncludeProperties(value = {"id"})
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id", referencedColumnName = "id")
    private User owner;
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ElementCollection
    @CollectionTable(name = "inodes_to_children")
    @MapKeyJoinColumn(name = "inode_name")
    @Column(name = "inode_id")
    protected Map<String,INode> children;   //key contains the file extension.

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "users_roles")
    @MapKeyJoinColumn(name = "user")
    @Column(name = "user_id")
    protected Map<User, UserRole> roles;


    @JsonIgnore
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    protected INode parent;

    INode(){

    }

    public INode(String name, INodeType type, LocalDateTime creationDate, User owner, Map<String, INode> children, INode parent) {
        this.name = name;
        this.type = type;
        this.creationDate = creationDate;
        this.owner = owner;
        this.children = children;
        this.parent = parent;
    }

//    public INode(String name, INodeType type, LocalDateTime creationDate, Set<INode> children, INode parent) {
//        this.name = name;
//        this.type = type;
//        this.creationDate = creationDate;
//        this.children = children;
//        this.parent = parent;
//    }

//    public INode(Long id, String name, INodeType type, LocalDateTime creationDate, Set<INode> children, INode parent) {
//        this.id = id;
//        this.name = name;
//        this.type = type;
//        this.creationDate = creationDate;
//        this.children = children;
//        this.parent = parent;
//    }

    public static INode createNewDirectory(String name, INode parent, User owner) {
        return new INode(name, INodeType.DIR, LocalDateTime.now(), owner, new HashMap<>(), parent);
    }

    public static INode createRootDir(User user) {
        return createNewDirectory("root_" + user.getEmail(), null, user);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public INodeType getType() {
        return type;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public User getOwner() {
        return owner;
    }

    public Map<String, INode> getChildren() {
        return children;
    }

    public INode getParent() {
        return parent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(INode parent) {
        this.parent = parent;
    }

    public void setType(INodeType type) {
        this.type = type;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setChildren(Map<String, INode> children) {
        this.children = children;
    }

    public Map<User, UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Map<User, UserRole> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof INode)) return false;

        INode iNode = (INode) o;

        if (!Objects.equals(id, iNode.id)) return false;
        if (!Objects.equals(name, iNode.name)) return false;
        if (type != iNode.type) return false;
        if (!Objects.equals(creationDate, iNode.creationDate)) return false;
        if (!Objects.equals(owner, iNode.owner)) return false;
        if (!Objects.equals(children, iNode.children)) return false;
        return Objects.equals(parent, iNode.parent);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }
}
