package docSharing.fileSystem;

import docSharing.enums.INodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class addINodeRequest {

    private Long parentId;
    private String name;
    private INodeType type;
}
