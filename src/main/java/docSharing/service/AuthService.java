package docSharing.service;

import com.google.gson.Gson;
import docSharing.UserDTO.UserDTO;
import docSharing.entities.User;

import docSharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static docSharing.entities.User.createUserFactory;

@Service
public class AuthService {

    Map<String,String> tokens= new HashMap<>();
    @Autowired
    private UserRepository userRepository;

    private static final Gson gson = new Gson();
    public AuthService() {}

    public User createUser(String name, String email, String password) {
       if (userRepository.findByEmail(email)!= null)
           throw new IllegalArgumentException("the user has already registered");

        User user = createUserFactory(name, email, password);
        userRepository.save(user);
        return user;
    }


    private boolean isExistingEmail (String email)
    {
        User user = userRepository.findByEmail(email);
        return (user!=null)?true:false;
    }

    public String generateToken()
    {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }


    public String login(String email, String password) {

        String token = isValidCredentials(email, password) ? generateToken() : null;

        if (token != null) {
            tokens.put(email, token);
        }

        return token;
    }

    public boolean isValidToken(String email, String token) {
        return tokens.get(email).compareTo(token) == 0;
    }

    public UserDTO updateTokenEmailKey(UserDTO user, String newEmail) {
        tokens.put(newEmail, tokens.get(user.email));
        tokens.remove(user.email);
        return user;
    }

    private boolean isValidCredentials(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user!= null) {
            return user.getPassword().equals(password);
        }

        return false;
    }

    public void updateTokenEmailKey(String oldEmail, String newEmail) {
        tokens.put(newEmail, tokens.get(oldEmail));
        tokens.remove(oldEmail);
    }


}
