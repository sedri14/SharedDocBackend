package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "fs_inodes")
@Inheritance(strategy = InheritanceType.JOINED)
public class INode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    @Column(name = "name", nullable = false)
    protected String name;
    @Enumerated(EnumType.STRING)
    protected INodeType type;
    @Column(name = "creation_date")
    private LocalDate creationDate;
    @OneToMany(mappedBy = "parent")
    protected Set<INode> children;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    protected INode parent;

    INode(){

    }

    public INode(String name, INodeType type, LocalDate creationDate, Set<INode> children, INode parent) {
        this.name = name;
        this.type = type;
        this.creationDate = creationDate;
        this.children = children;
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public INode getParent() {
        return parent;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(INode parent) {
        this.parent = parent;
    }
}
