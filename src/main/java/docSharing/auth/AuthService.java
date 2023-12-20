package docSharing.auth;

import docSharing.utils.SiteIdGenerator;
import docSharing.entities.INode;
import docSharing.user.UserType;
import docSharing.exceptions.IllegalOperationException;
import docSharing.exceptions.UserNotFoundException;
import docSharing.filters.JwtService;
import docSharing.user.User;
import docSharing.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());


    public AuthenticationResponse register(RegisterRequest request) {

        Optional<User> userByEmail = userRepository.findByEmail(request.getEmail());
        if (userByEmail.isPresent()) {
            throw new IllegalOperationException(String.format("User '%s' already exists", request.getEmail()));
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .siteId(SiteIdGenerator.generateSiteId(request.getEmail()))
                .userType(UserType.USER)
                .build();

        INode rootDir = INode.createUserRootDirectory(user);
        user.setRootDirectory(rootDir);
        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .rootId(rootDir.getId())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFoundException::new);
            String jwtToken = jwtService.generateToken(user);
            logger.info("JWT: {}", jwtToken);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .email(user.getEmail())
                    .rootId(user.getRootDirectory().getId())
                    .build();

        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}", request.getEmail());
            throw new BadCredentialsException(e.getMessage());
        }
    }
}
