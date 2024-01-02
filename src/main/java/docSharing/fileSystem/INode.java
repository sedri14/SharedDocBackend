package docSharing.fileSystem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import docSharing.entities.BaseEntity;
import docSharing.documentUserAccess.DocumentUserAccess;
import docSharing.enums.INodeType;
import docSharing.user.User;
import lombok.*;
import org.hibernate.Hibernate;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inodes")
@Inheritance(strategy = InheritanceType.JOINED)
public class INode extends BaseEntity implements Serializable {

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
    @ToString.Exclude
    private List<DocumentUserAccess> sharedItems;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        INode iNode = (INode) o;
        return getId() != null && Objects.equals(getId(), iNode.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
