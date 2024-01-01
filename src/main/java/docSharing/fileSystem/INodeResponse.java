package docSharing.fileSystem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class INodeResponse implements Serializable {

    private Long id;
    private String name;
    private String type;
    private LocalDateTime creationDate;

    protected INodeResponse(INode inode) {
        this.id = inode.getId();
        this.name = inode.getName();
        this.type = inode.getType().toString();
        this.creationDate = inode.getCreationDate();
    }
}
