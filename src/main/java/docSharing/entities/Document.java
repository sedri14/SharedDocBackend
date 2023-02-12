package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import docSharing.enums.INodeType;
import docSharing.enums.UserRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "Document")
public class Document extends INode {


    @Column(name = "last_edited")
    private LocalDateTime lastEdited;
    @Lob
    @Column(name = "content")
    private String content;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private Set<Log> log = new HashSet<>();

    Document() {
        super();
    }


    public Document(String name, INodeType type, LocalDateTime creationDate, Set<INode> children, INode parent, User owner, LocalDateTime lastEdited, String content) {
        super(name, type, creationDate, owner, null, parent);
        this.lastEdited = lastEdited;
        this.content = content;
    }

    public static Document createNewImportedDocument(String nameWithExtension, String content, INode parent, User owner) {
        return new Document(nameWithExtension, INodeType.FILE, LocalDateTime.now(), null, parent, owner, LocalDateTime.now(), content);
    }

    public static Document createNewEmptyDocument(String name, INode parent, User owner) {
        return new Document(name, INodeType.FILE, LocalDateTime.now(), null, parent, owner, LocalDateTime.now(), "");
    }

    public LocalDateTime getLastEdited() {
        return lastEdited;
    }

    public String getContent() {
        return content;
    }

    public void setLastEdited(LocalDateTime lastEdited) {
        this.lastEdited = lastEdited;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
