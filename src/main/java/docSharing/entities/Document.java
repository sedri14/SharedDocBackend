package docSharing.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name="Document")
public class Document extends INode {

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id", referencedColumnName = "id")
    private User owner;

    @Column(name = "last_edited")
    private LocalDate lastEdited;

    //TODO: check how to save a very long string.
    @Column(name = "content")
    private String content;

//    @OneToMany
//    private List<User> editorUsers;

//    @OneToMany
//    private List<User> viewerUsers; //should it be here on server??? this is online only
    //we should change the path for the document each time
    //private String path;


    //isDocumentPrivate. if private just who is in the viewing list and the editing list can access it, else get an error message

    Document (){
        super();

    }

    public Document(String name, INodeType type, LocalDate creationDate, Set<INode> children, INode parent, User owner, LocalDate lastEdited, String content) {
        super(name, type, creationDate, children, parent);
        this.owner = owner;
        this.lastEdited = lastEdited;
        this.content = content;
    }

    public User getOwner() {
        return owner;
    }
}
