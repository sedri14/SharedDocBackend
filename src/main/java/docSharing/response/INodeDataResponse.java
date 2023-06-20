package docSharing.response;

import java.util.List;

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

    public List<PathItem> getPath() {
        return path;
    }

    public List<INodeResponse> getChildren() {
        return children;
    }
}
