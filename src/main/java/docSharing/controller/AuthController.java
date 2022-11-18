package docSharing.controller;

import com.google.gson.Gson;
import docSharing.Utils.Validation;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private AuthService authService;
    private static final Gson gson = new Gson();

    private static Logger logger = LogManager.getLogger(AuthController.class.getName());


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody User user) {
        logger.debug("in AuthenticationController.register() - int Level:500");

        if (!Validation.isValidEmail(user.getEmail())) {
            logger.error("In AuthenticationController.register: invalid email - int Level:200");
            throw new IllegalArgumentException("Invalid email address!");
        }
        if (!Validation.isValidName(user.getName())) {
            logger.error("In AuthenticationController.register: invalid name - int Level:200");
            throw new IllegalArgumentException("Invalid name!");
        }
        if (!Validation.isValidPassword(user.getPassword())) {
            logger.error("In AuthenticationController.register: invalid password - int Level:200");
            throw new IllegalArgumentException("Invalid password!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(authService.createUser(user.getName(), user.getEmail(), user.getPassword())));

    }




}
