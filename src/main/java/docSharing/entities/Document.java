package docSharing.entities;

import java.time.LocalDate;
import java.util.List;

public class Document {
    private String name;
    private User owner;
    private LocalDate creationDate;

    private List<User> editorUsers;
    private List<User> viewerUsers;
    private String path;



}
