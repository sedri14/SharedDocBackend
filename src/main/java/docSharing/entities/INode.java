package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
//@DiscriminatorValue("0")
@Table(name = "fs_inodes")
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
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent" , cascade = CascadeType.REMOVE)
    protected Set<INode> children;
    @JsonIgnore
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    protected INode parent;

    INode(){

    }

    public INode(String name, INodeType type, LocalDateTime creationDate, Set<INode> children, INode parent) {
        this.name = name;
        this.type = type;
        this.creationDate = creationDate;
        this.children = children;
        this.parent = parent;
    }

    public static INode createNewDirectory(String name, INode parent) {
        return new INode(name, INodeType.DIR, LocalDateTime.now(), null, parent);
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

    public Set<INode> getChildren() {
        return children;
    }

    public INode getParent() {
        return parent;
    }

//    public void setId(Long id) {
//        this.id = id;
//    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(INode parent) {
        this.parent = parent;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        INode iNode = (INode) o;
//        return Objects.equals(id, iNode.id) && Objects.equals(name, iNode.name) && type == iNode.type && Objects.equals(children, iNode.children) && Objects.equals(parent, iNode.parent);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, name, type, children, parent);
//    }
}
