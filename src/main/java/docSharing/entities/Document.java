package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import docSharing.CRDT.CharItem;
import docSharing.enums.INodeType;
import docSharing.fileSystem.INode;
import docSharing.user.User;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Document")
public class Document extends INode {

    @Column(name = "last_edited")
    private LocalDateTime lastEdited;

    //Logoot CRDT
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CharItem> content;

    private long logicalSize;

    public Document(String name, INodeType type, LocalDateTime creationDate, INode parent, User owner, List<CharItem> content, LocalDateTime lastEdited) {
        super(name, type, creationDate, owner, null, parent, null);
        this.content = content;
        this.lastEdited = lastEdited;
    }
}
