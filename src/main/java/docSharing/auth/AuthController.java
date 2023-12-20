package docSharing.auth;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    private static final Logger logger = LogManager.getLogger(AuthController.class.getName());

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest) {
        logger.debug("Registering a new user {}", registerRequest.getEmail());
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @RequestMapping(value = "authenticate", method = RequestMethod.POST)
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        logger.debug("Authentication {}", authenticationRequest.getEmail());
        return ResponseEntity.ok(authService.authenticate(authenticationRequest));

    }
}
