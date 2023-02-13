package docSharing.service;

import com.google.gson.Gson;
import docSharing.DTO.User.UserDTO;
import docSharing.entities.User;
import docSharing.repository.TokenRepository;
import docSharing.repository.UserRepository;
import docSharing.response.LoginEnum;
import docSharing.response.LoginObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.util.*;
import static docSharing.response.LoginObject.createLoginObject;

@Service
public class AuthService {

    private static Map<String, User> userByToken = new HashMap<>();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());

    private static final int SCHEDULE = 1000 * 60 * 60;

    private static final Gson gson = new Gson();

    public AuthService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public AuthService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public AuthService() {
    }


    public User register(UserDTO userDTO) {
        User newUser = User.createNewUserFromUserDTO(userDTO);
        return userRepository.save(newUser);
    }


    private boolean isExistingEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }


    public LoginObject login(UserDTO userDTO) {
        logger.info("in login");

        User user = userRepository.findByEmail(userDTO.getEmail());
        if (user == null)
            return createLoginObject(user.getId(), null, String.valueOf(LoginEnum.EMAIL_NOT_EXIST.toString()), user.getName());
        if (!user.getPassword().equals(userDTO.getPassword()))
            return createLoginObject(user.getId(), null, String.valueOf(LoginEnum.INVALID_PASSWORD.toString()), user.getName());
        else {  //login credentials ok
            userByToken.put(generateToken(), user);
            LoginObject loginObject = createLoginObject(user.getId(), "remember to destroy this stupid loginObject", null, user.getName());
            return loginObject;
        }
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
