package docSharing.service;

import docSharing.Utils.GenerateToken;
import docSharing.Utils.Validation;
import docSharing.entities.User;
import docSharing.repository.AuthRepository;
import docSharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Service
public class AuthService {

    Map<String,String> tokens= new HashMap<>();
    @Autowired
    private UserRepository userRepository;

//    public User createUser(String name, String email, String password) {
//        if (UserRepository.isExist(email)) {
//            throw new IllegalArgumentException("the user has already registered");
//        }
//        User user = createUser(name, email, password);
//        userRepository.save(user);
//        return user;
//    }
//
//
//    public String login(User user, String password) {
//        String token = isValidCredentials(user, password) ? GenerateToken.generateToken() : null;
//
//        if (token != null) {
//            tokens.put(user.getEmail(),token);
//
//        }
//        return token;
//    }

    private boolean isValidCredentials(User user, String password) {
        Optional<User> newUser = Optional.of(UserRepository.findByEmail(user.getEmail()));
       if (!newUser.equals(Optional.empty()) && user.getPassword().equals(password))
           return true;
        else return false;
    }

}
