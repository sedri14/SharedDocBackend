package docSharing.service;

import com.google.gson.Gson;
import docSharing.DTO.User.LogInUserResponse;
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

        return userRepository.save(newUser);
    }


    private boolean isExistingEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }


    public LogInUserResponse login(UserDTO userDTO) {
        logger.info("in login");

        User user = userRepository.findByEmail(userDTO.getEmail());
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

    // ------------------ verification token ------------------

//    public boolean isValidToken(String email, String token) {
//        return mapUserTokens.get(email).compareTo(token) == 0;
//    }

//    public boolean isValidToken(Long userId, String token) {
//        return mapUserTokens.get(userId).compareTo(token) == 0;
//    }
//
//    public UserDTO updateTokenEmailKey(UserDTO user, String newEmail) {
//        User createduser = createUserFactory(user);
//        mapUserTokens.put(createduser.getId(), mapUserTokens.get(user.getEmail()));
//        mapUserTokens.remove(user.getEmail());
//        return user;
//    }

//    private boolean isValidCredentials(String email, String password) {
//        User user = userRepository.findByEmail(email);
//
//        if (user != null) {
//            return user.getPassword().equals(password);
//        }
//
//        return false;
//    }

//    public User getUser(String verificationToken) {
//        User user = tokenRepository.findByToken(verificationToken).getUser();
//        return user;
//    }

//    public void createVerificationToken(User user, String token) {
//        VerificationToken myToken = new VerificationToken(token, user);
//        tokenRepository.save(myToken);
//    }


//    public void publishRegistrationEvent(UserDTO createdUser, Locale locale, String appUrl) {
//        User user = userRepository.findByEmail(createdUser.getEmail());
//        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, locale, appUrl));
//        System.out.println("inside publishRegistrationEvent");
//    }

//    public void deleteVerificationToken(String token) {
//        tokenRepository.deleteByToken(token);
//    }
//
//    public VerificationToken getVerificationToken(String VerificationToken) {
//        return tokenRepository.findByToken(VerificationToken);
//    }


//    @Scheduled(fixedRate = SCHEDULE)
//    public void scheduleDeleteNotActivatedUsers() {
//        logger.info("---------- in scheduleDeleteNotActivatedUsers-------------");
//        List<VerificationToken> tokens = tokenRepository.findAll();
//
//        Calendar cal = Calendar.getInstance();
//        List<VerificationToken> expiredTokens = tokens.stream().
//                filter(token -> token.getExpiryDate().getTime() - cal.getTime().getTime() <= 0)
//                .collect(Collectors.toList());
//
//        for (VerificationToken token : expiredTokens) {
//            deleteVerificationToken(token.getToken());
//            userRepository.deleteById(token.getUser().getId());
//            logger.debug("verification token for user_id#" + token.getUser().getId() + " and non activated user was deleted");
//        }
//    }
//
//    public void saveRegisteredUser(User user) {
//        userRepository.save(user);
//    }


}
