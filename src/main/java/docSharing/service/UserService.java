package docSharing.service;
import docSharing.entities.User;
import docSharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.logging.Logger;


@Service
public class UserService  {

    @Autowired
    private UserRepository userRepository;

    // logger
    private Logger logger = Logger.getLogger(getClass().getName());

    public UserService() {}

    public User updateUserName(String email, String name) {
        User user = userRepository.findByEmail(email);

        if (user!= null) {
            user.setName(name);
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException(String.format("Email address: %s does not exist", email));
        }
    }

    public User updateUserEmail(String email, String newEmail) {
        User user = userRepository.findByEmail(email);

        if (user!= null) {
            user.setEmail(newEmail);
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException(String.format("Email address: %s does not exist", email));
        }
    }

    public User updateUserPassword(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user!= null) {
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


    public User findByEmail(String email)
    {
        return userRepository.findByEmail(email);
    }

//    //--------------------------------------------------------------------------------
//    @Override
//    @Transactional
//    public User registerUser(UserDTO userDto) {
//
//        User user = new User();
//        user.setName(userDto.getName());
//        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
//        user.setPassword(hashedPassword);
//        user.setEnabled(userDto.isEnabled());
//        user.setEmail(userDto.getEmail());
//
//        user.setRoles(Arrays.asList(roleDAO.findByRoleName("ROLE_CANDIDATE")));
//        userDAO.save(user);
//        return user;
//    }
//
//    @Override
//    public User findByUsername(String username) {
//        return UserRepository.findByName(username);
//    }
//
//    @Transactional
//    @Override
//    public User loginUser(UserDTO userDTO) {
//        return userDAO.loginUser(userDTO);
//    }
//
//    @Override
//    public User findByUsernameAndPassword(String username, String password) {
//        return UserRepository.findByUsernameAndPassword(username, password);
//    }
//
//    @Override
//    public void createVerificationToken(User user, String token) {
//        VerificationToken newUserToken = new VerificationToken(token, user);
//        tokenDAO.save(newUserToken);
//    }
//
//    @Override
//    @Transactional
//    public VerificationToken getVerificationToken(String verificationToken) {
//        return UserRepository.findByToken(verificationToken);
//    }
//
//    @Override
//    @Transactional
//    public void enableRegisteredUser(User user) {
//        userDAO.save(user);
//    }
//
//



}
