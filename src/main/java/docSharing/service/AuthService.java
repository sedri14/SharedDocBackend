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

    public User createUser(String name, String email, String password) {
        if (UserRepository.isExist(email)) {
            throw new IllegalArgumentException("the user has already registered");
        }
        User user = createUser(name, email, password);
        userRepository.save(user);
        return user;
    }



}
