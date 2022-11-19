package docSharing.service;

import docSharing.entities.User;
import docSharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User updateUserName(String email, String name) {
        User user = UserRepository.findByEmail(email);

        if (user!= null) {
            user.setName(name);
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException(String.format("Email address: %s does not exist", email));
        }
    }

    public User updateUserEmail(String email, String newEmail) {
        User user = UserRepository.findByEmail(email);

        if (user!= null) {
            user.setEmail(newEmail);
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException(String.format("Email address: %s does not exist", email));
        }
    }

    public User updateUserPassword(String email, String password) {
        User user = UserRepository.findByEmail(email);

        if (user!= null) {
            user.setPassword(password);
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException(String.format("Email address: %s does not exist", email));
        }
    }


    public void deleteUser(String email) {
        User user = UserRepository.findByEmail(email);

        if (user != null) {
            userRepository.delete(user);
        } else {
            throw new IllegalArgumentException(String.format("Email address %s does not match any user", email));
        }
    }



}
