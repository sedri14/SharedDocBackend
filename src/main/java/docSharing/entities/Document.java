package docSharing.entities;

import java.time.LocalDate;
import java.util.List;

public class Document {
    private String name;
    private User owner;
    private LocalDate creationDate;
    //last editing time
    //

    private List<User> editorUsers;
    private List<User> viewerUsers;
    //we should change the path for the document each time
    private String path;
    //isDocumentPrivate. if private just who is in the viewing list and the editing list can access it, else get an error message


}
