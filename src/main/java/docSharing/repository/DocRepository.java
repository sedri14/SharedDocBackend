package docSharing.repository;

import docSharing.entities.Document;
import docSharing.entities.INode;
import docSharing.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DocRepository extends JpaRepository<Document, Long> {

//    create new document
//    {
//      boolean  isExist(User user)
//    }


    //function: check if document exist for that user
    //  boolean  isExist(User user)

    //create new document

    //change user permmision (Permission ,User)

    //insert viewer to viewer list
    // insert editor to editor list

    //boolean isEditor(User user)
    //boolean isViewer(User user)

    // updateUsersList


}
