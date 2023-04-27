package docSharing.entities;

import docSharing.CRDT.CRDT;
import docSharing.enums.INodeType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Document")
public class Document extends INode {


    @Column(name = "last_edited")
    private LocalDateTime lastEdited;

    //A tree data structure that stores the document content
    @OneToOne
    private CRDT crdt;

    Document() {
        super();
    }


    public Document(String name, INodeType type, LocalDateTime creationDate, Set<INode> children, INode parent, User owner, CRDT crdt, LocalDateTime lastEdited) {
        super(name, type, creationDate, owner, null, parent);
        this.crdt = crdt;
        this.lastEdited = lastEdited;
    }

    public static Document createNewImportedDocument(String nameWithExtension, INode parent, CRDT crdt, User owner) {
        return new Document(nameWithExtension, INodeType.FILE, LocalDateTime.now(), null, parent, owner, crdt, LocalDateTime.now());
    }

    public static Document createNewEmptyDocument(String name, INode parent, User owner) {
        CRDT emptyDocTreeCRDT = new CRDT();
        return new Document(name, INodeType.FILE, LocalDateTime.now(), null, parent, owner, emptyDocTreeCRDT, LocalDateTime.now());
    }

    public LocalDateTime getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(LocalDateTime lastEdited) {
        this.lastEdited = lastEdited;
    }

    public CRDT getCrdt() {
        return crdt;
    }
}
