package docSharing.service;

import docSharing.entities.User;
import docSharing.repository.AuthRepository;
import docSharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.Optional;
@Service
public class AuthService {


    @Autowired
    private UserRepository userRepository;


    public User addUser(User user) throws SQLDataException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new SQLDataException(String.format("Email %s exists in users table", user.getEmail()));
        }
        return userRepository.save(user);
    }


}
