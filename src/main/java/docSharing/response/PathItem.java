package docSharing.response;

public class PathItem {

    Long id;
    String name;

    public PathItem(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

