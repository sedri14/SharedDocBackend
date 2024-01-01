package docSharing.fileSystem;

import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildrenDataResponse {

    private List<BreadCrumb> path;

    private List<INodeResponse> children;
}
