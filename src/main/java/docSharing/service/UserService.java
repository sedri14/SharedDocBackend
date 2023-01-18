package docSharing.service;

import docSharing.controllers.AuthController;
import docSharing.entities.User;
import docSharing.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // logger
    private static Logger logger = LogManager.getLogger(AuthController.class.getName());

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

        boolean isPresent = userRepository.findById(id).isPresent();

        if (!isPresent) {
            throw new IllegalArgumentException("User not found");
        }

        return userRepository.findById(id).get();
    }

    public Optional<User> updateEnabled(Long id, Boolean enabled) {
        int lines = userRepository.updateUserEnabledById(id, enabled);
        logger.debug("lines updated: " + lines);

        return getUpdatedUser(id, lines);
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
