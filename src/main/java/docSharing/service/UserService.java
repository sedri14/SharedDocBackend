package docSharing.service;

import docSharing.entities.User;
import docSharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.logging.Logger;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // logger
    private Logger logger = Logger.getLogger(getClass().getName());

    public UserService() {
    }

    public User updateUserName(String email, String name) {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            user.setName(name);
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException(String.format("Email address: %s does not exist", email));
        }
    }


    public User updateUserEmail(Long id, String newEmail) {
        try {
            User user = userRepository.getReferenceById(id);
            user.setEmail(newEmail);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new IllegalArgumentException("User doesn't exist");
        }
    }

    public User updateUserPassword(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            user.setPassword(password);
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException(String.format("Email address: %s does not exist", email));
        }
    }


    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            userRepository.delete(user);
        } else {
            throw new IllegalArgumentException(String.format("Email address %s does not match any user", email));
        }
    }


    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getById(Long id) {
        User user = null;
        try {
            user = userRepository.getReferenceById(id);
        } catch (EntityNotFoundException e) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }
}
