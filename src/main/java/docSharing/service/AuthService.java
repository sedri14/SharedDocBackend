package docSharing.service;

import com.google.gson.Gson;
import docSharing.response.LogInUserResponse;
import docSharing.DTO.User.UserDTO;
import docSharing.entities.INode;
import docSharing.entities.User;
import docSharing.exceptions.InvalidFormatException;
import docSharing.repository.TokenRepository;
import docSharing.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class AuthService {

    private static Map<String, User> userByToken = new HashMap<>();
    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());

    private static final int SCHEDULE = 1000 * 60 * 60;

    private static final Gson gson = new Gson();

    public AuthService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
    }

    public AuthService() {
    }


    public User register(UserDTO userDTO) {
        User newUser = User.createNewUserFromUserDTO(userDTO);
        INode rootDir = INode.createRootDir(newUser);
        newUser.setRootDirectory(rootDir);
        newUser.setSiteId(Objects.hashCode(userDTO.email));

        return userRepository.save(newUser);
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }


    public LogInUserResponse login(UserDTO userDTO) {
        logger.info("in login");

        User user = userRepository.findByEmail(userDTO.getEmail()).get();
        if (!user.getPassword().equals(userDTO.getPassword())) {

            throw new InvalidFormatException(userDTO.getEmail());
        }
        String token = generateToken();
        userByToken.put(token, user);
        logger.info(token);

        return new LogInUserResponse(token, userDTO.email, user.getRootDirectory().getId());
    }

    public User getCachedUser(String token) {
        return userByToken.get(token);
    }

}
