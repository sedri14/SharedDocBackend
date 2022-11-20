package docSharing.service;

import docSharing.entities.User;
import docSharing.entities.UserDTO;
import docSharing.entities.VerificationToken;
import org.springframework.stereotype.Service;


@Service
public interface IUserService extends UserDetailsService {
    public User registerUser(UserDTO userDto);

    public User findByUsername(String username);

    public User loginUser(UserDTO userDTO);

    public User findByUsernameAndPassword(String username, String password);

    public void createVerificationToken(User user, String token);

    public VerificationToken getVerificationToken(String verificationToken);

    public void enableRegisteredUser(User user);
}