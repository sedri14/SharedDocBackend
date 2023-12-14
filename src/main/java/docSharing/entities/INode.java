package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import docSharing.enums.INodeType;
import docSharing.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

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
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name="owner_id", referencedColumnName = "id")
    private User owner;
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ElementCollection
    @CollectionTable(name = "inodes_to_children")
    @MapKeyJoinColumn(name = "inode_name")
    @Column(name = "inode_id")
    protected Map<String,INode> children;   //key contains the file extension.

    @JsonIgnore
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    protected INode parent;

    @JsonIgnore
    @OneToMany(mappedBy = "inode", cascade = CascadeType.REMOVE)
    private List<SharedRole> sharedItems;


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

    public static INode createNewDirectory(String name, INode parent, User owner) {
        return new INode(name, INodeType.DIR, LocalDateTime.now(), owner, new HashMap<>(), parent);
    }

    public static INode createUserRootDirectory(User user) {
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

    public List<SharedRole> getSharedItems() {
        return sharedItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof INode)) return false;

        INode iNode = (INode) o;

        if (!Objects.equals(id, iNode.id)) return false;
        if (!Objects.equals(name, iNode.name)) return false;
        return type == iNode.type;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
