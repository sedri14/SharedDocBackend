package docSharing.entities;

import docSharing.CRDT.CharItem;
import docSharing.enums.INodeType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Document")
public class Document extends INode {


    @Column(name = "last_edited")
    private LocalDateTime lastEdited;

    //Logoot CRDT
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CharItem> content;

    private long logicalSize;

    Document() {
        super();
    }


    public Document(String name, INodeType type, LocalDateTime creationDate, Set<INode> children, INode parent, User owner, List<CharItem> content, LocalDateTime lastEdited) {
        super(name, type, creationDate, owner, null, parent);
        this.content = content;
        this.lastEdited = lastEdited;
    }

//    public static Document createNewImportedDocument(String nameWithExtension, INode parent, CRDT crdt, User owner) {
//        return new Document(nameWithExtension, INodeType.FILE, LocalDateTime.now(), null, parent, owner, crdt, LocalDateTime.now());
//    }

    public static Document createNewEmptyDocument(String name, INode parent, User owner) {
        List<CharItem> emptyContent = new ArrayList<>();
        return new Document(name, INodeType.FILE, LocalDateTime.now(), null, parent, owner, emptyContent, LocalDateTime.now());
    }

    public void setLastEdited(LocalDateTime lastEdited) {
        this.lastEdited = lastEdited;
    }

    public void incSize() {
        this.logicalSize++;
    }


}
