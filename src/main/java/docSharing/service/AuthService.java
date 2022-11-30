package docSharing.service;

//import EmailActivation.OnRegistrationCompleteEvent;

import com.google.gson.Gson;
import docSharing.UserDTO.UserDTO;
import docSharing.emailActivation.OnRegistrationCompleteEvent;
import docSharing.entities.User;
import docSharing.entities.VerificationToken;
import docSharing.repository.TokenRepository;
import docSharing.repository.UserRepository;
import docSharing.response.LoginEnum;
import docSharing.response.LoginObject;
import docSharing.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.*;
import java.util.stream.Collectors;

import static docSharing.entities.User.createUserFactory;
import static docSharing.response.LoginObject.createLoginObject;

@Service
public class AuthService {

    static Map<User,String> mapUserTokens = new HashMap<>();
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

    public AuthService() {}


    public Response<UserDTO> createUser(UserDTO user) throws SQLDataException {
        logger.info("in createUser");
        if (!isExistingEmail(user.getEmail()))
        {
            userRepository.save(createUserFactory(user));
            return Response.success(user);
        }
        else
            return Response.failure(String.format("Email %s exists in users table", user.getEmail()));

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


    public LoginObject login(UserDTO user) {
        logger.info("in login");

        User userByEmail = userRepository.findByEmail(user.getEmail());
        if (userByEmail == null) //User doesn't exist
            return createLoginObject(userByEmail.getId(),null, String.valueOf(LoginEnum.EMAIL_NOT_EXIST));
        if (!isEnabledUser(user))
            return createLoginObject(userByEmail.getId(),null, String.valueOf(LoginEnum.CONFIRM_EMAIL));
        if (!userByEmail.getPassword().equals(user.getPassword()))  //User exist check password
            return createLoginObject(userByEmail.getId(),null, String.valueOf(LoginEnum.INVALID_PASSWORD));
        else
        {
            String token = generateToken();
            mapUserTokens.put(userByEmail, token);
            LoginObject loginObject=createLoginObject(userByEmail.getId(),token,null);
            return  loginObject;
        }
    }

    public boolean isEnabledUser(UserDTO user) {
        logger.info("in isEnabledUser");

        User userByEmail = userRepository.findByEmail(user.getEmail());

        if (userByEmail == null) {
            return false;
        }

        return userByEmail.isEnabled();
    }

    // ------------------ verification token ------------------

    public boolean isValidToken(String email, String token) {
        return mapUserTokens.get(email).compareTo(token) == 0;
    }

    public UserDTO updateTokenEmailKey(UserDTO user, String newEmail) {
        User createduser= createUserFactory(user);
        mapUserTokens.put(createduser, mapUserTokens.get(user.getEmail()));
        mapUserTokens.remove(user.getEmail());
        return user;
    }

    private boolean isValidCredentials(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user!= null) {
            return user.getPassword().equals(password);
        }

        return false;
    }

    public User getUser(String verificationToken) {
        User user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }


    public void publishRegistrationEvent(User createdUser, Locale locale, String appUrl  ) {
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(createdUser, locale, appUrl));
        System.out.println("inside publishRegistrationEvent");
    }

    public void deleteVerificationToken(String token) {
        tokenRepository.deleteByToken(token);
    }
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }


    @Scheduled(fixedRate = SCHEDULE)
    public void scheduleDeleteNotActivatedUsers() {
        logger.info("---------- in scheduleDeleteNotActivatedUsers-------------");
        List<VerificationToken> tokens = tokenRepository.findAll();

        Calendar cal = Calendar.getInstance();
        List<VerificationToken> expiredTokens = tokens.stream().
                filter(token -> token.getExpiryDate().getTime() - cal.getTime().getTime() <= 0)
                .collect(Collectors.toList());

        for (VerificationToken token: expiredTokens) {
            deleteVerificationToken(token.getToken());
            userRepository.deleteById(token.getUser().getId());
            logger.debug("verification token for user_id#" + token.getUser().getId() + " and non activated user was deleted");
        }
    }

    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }



}
