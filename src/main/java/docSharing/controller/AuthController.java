package docSharing.controller;

import docSharing.entities.User;
import docSharing.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLDataException;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    private static Logger logger = LogManager.getLogger(AuthController.class.getName());


    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            return new ResponseEntity<>(authService.addUser(user).toString(), HttpStatus.OK);
        } catch (SQLDataException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email already exists", e);
        }
    }


    public boolean isValidPassword(String password) {
        logger.debug("in AuthenticationController.isValidPassword() - int Level:500");

        return password.matches(".*[A-Z].*") && password.length() >= 6;
    }

    public boolean isValidName(String Name) {
        logger.debug("in AuthenticationController.isValidName() - int Level:500");

        return Name.matches("^[ A-Za-z]+$");
    }

    public static boolean isValidEmail(String emailAddress) {
        logger.debug("in AuthenticationController.isValidEmail() - int Level:500");

        String regexPattern = "^(.+)@(\\S+)$";

        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
