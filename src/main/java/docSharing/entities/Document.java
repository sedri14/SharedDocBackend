package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Document")
public class Document extends INode {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @Column(name = "last_edited")
    private LocalDateTime lastEdited;
    @Lob
    @Column(name = "content")
    private String content;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private Set<Permission> permissions = new HashSet<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private Set<Log> log = new HashSet<>();
//    @OneToMany
//    private List<User> editorUsers;

//    @OneToMany
//    private List<User> viewerUsers; //should it be here on server??? this is online only
    //we should change the path for the document each time
    //private String path;


    //isDocumentPrivate. if private just who is in the viewing list and the editing list can access it, else get an error message

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
