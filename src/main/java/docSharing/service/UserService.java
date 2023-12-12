package docSharing.service;

import docSharing.entities.User;
import docSharing.exceptions.UserNotFoundException;
import docSharing.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // logger
    private static final Logger logger = LogManager.getLogger(UserService.class.getName());

    public UserService() {
    }

    public User fetchUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public User updateUserName(String email, String name) {
        User user = fetchUserByEmail(email);
        user.setName(name);

        return userRepository.save(user);
    }

    public User updateUserPassword(String email, String password) {
        User user = fetchUserByEmail(email);
        user.setPassword(password);

        return userRepository.save(user);
    }

    public void deleteUser(String email) {
        User user = fetchUserByEmail(email);
        userRepository.delete(user);
    }
}
