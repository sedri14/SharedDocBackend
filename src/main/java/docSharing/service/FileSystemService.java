package docSharing.service;

import docSharing.entities.User;
import docSharing.entities.abstracts.INode;
import docSharing.repository.FileSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.List;

@Service
public class FileSystemService {

    @Autowired
    private FileSystemRepository fsRepository;

    //private repo
    public List<INode> getInodesInLevel(Long id) {

        return null;
    }


//    public User addUser(User user) throws SQLDataException {
//        if (userRepository.findByEmail(user.getEmail()) != null) {
//            throw new SQLDataException(String.format("Email %s exists in users table", user.getEmail()));
//        }
//        return userRepository.save(user);
//    }
}
