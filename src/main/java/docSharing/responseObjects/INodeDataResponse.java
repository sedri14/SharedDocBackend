package docSharing.responseObjects;

import docSharing.fileSystem.INodeResponse;
import lombok.Getter;
import java.util.List;

@Getter
public class INodeDataResponse {

    List<PathItem> path;
    List<INodeResponse> children;

    private INodeDataResponse(List<PathItem> path, List<INodeResponse> children) {
        this.path = path;
        this.children = children;
    }

    public static INodeDataResponse getChildrenWithPath(List<PathItem> path, List<INodeResponse> children) {
        return new INodeDataResponse(path, children);
    }
}
