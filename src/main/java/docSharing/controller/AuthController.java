package docSharing.controller;

import com.google.gson.Gson;
import docSharing.Utils.Validation;
import docSharing.UserDTO.UserDTO;
import docSharing.repository.UserRepository;
import docSharing.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;
    private static final Gson gson = new Gson();

    private static Logger logger = LogManager.getLogger(AuthController.class.getName());


    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody UserDTO user) {
        if (!Validation.isValidEmail(user.email)) {
            logger.error("In AuthenticationController.register: invalid email - int Level:200");
            throw new IllegalArgumentException("Invalid email address!");
        }
        if (!Validation.isValidName(user.name)) {
            logger.error("In AuthenticationController.register: invalid name - int Level:200");
            throw new IllegalArgumentException("Invalid name!");
        }
        if (!Validation.isValidPassword(user.password)) {
            logger.error("In AuthenticationController.register: invalid password - int Level:200");
            throw new IllegalArgumentException("Invalid password!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(authService.createUser(user.name, user.email, user.password)));
    }

    @RequestMapping(value = "token", method = RequestMethod.PATCH)
    public ResponseEntity<String> updateTokenEmailKey (@RequestBody UserDTO user, @RequestParam String newEmail)
    {
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(authService.updateTokenEmailKey(user ,newEmail)));
    }



    @RequestMapping(value = "login", method = RequestMethod.POST)//
    public ResponseEntity<String> logIn(@RequestBody UserDTO user) {

      //  logger.debug("in AuthenticationController.login() - int Level:500");

        if (!Validation.isValidEmail(user.email)) {
            logger.error("In AuthenticationController.login: invalid email - int Level:200");
            throw new IllegalArgumentException("Your email address is invalid!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(authService.login(user.email,user.password)));
    }

}


