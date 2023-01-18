package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Document")
public class Document extends INode {

    @JsonIncludeProperties(value = {"id"})
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id", referencedColumnName = "id")
    private User owner;

    @Column(name = "last_edited")
    private LocalDateTime lastEdited;
    @Lob
    @Column(name = "content")
    private String content;

    //Map<User,UserRole>
    @OneToMany (mappedBy = "document", cascade = CascadeType.ALL)
    private Set<Permission> permissions = new HashSet<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private Set<Log> log = new HashSet<>();

    Document() {
        super();
    }

    public Document(String name, INodeType type, LocalDateTime creationDate, Set<INode> children, INode parent, User owner, LocalDateTime lastEdited, String content) {
        super(name, type, creationDate, children, parent);
        this.owner = owner;
        this.lastEdited = lastEdited;
        this.content = content;
    }

    public static Document createNewImportedDocument(String nameWithExtension, String content, INode parent, User owner) {
        return new Document(nameWithExtension, INodeType.FILE, LocalDateTime.now(), null, parent, owner, LocalDateTime.now(), content);
    }

    public static Document createNewEmptyDocument(String name, INode parent, User owner) {
        return new Document(name, INodeType.FILE, LocalDateTime.now(), null, parent, owner, LocalDateTime.now(), "");
    }

    public User getOwner() {
        return owner;
    }

    public LocalDateTime getLastEdited() {
        return lastEdited;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
