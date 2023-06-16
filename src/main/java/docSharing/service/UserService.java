package docSharing.service;

import docSharing.controllers.AuthController;
import docSharing.entities.INode;
import docSharing.entities.User;
import docSharing.exceptions.INodeNotFoundException;
import docSharing.exceptions.UserNotFoundException;
import docSharing.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // logger
    private static Logger logger = LogManager.getLogger(UserService.class.getName());

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


    public User fetchUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (null == user) throw new UserNotFoundException();

        return user;
    }

    private Optional<User> getUpdatedUser(Long id, int lines) {
        if (lines == 1) {
            User user = userRepository.findById(id).get();
            logger.debug("User #" + id + " updated: " + user);
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
