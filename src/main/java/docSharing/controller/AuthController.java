package docSharing.controller;

import com.google.gson.Gson;
import docSharing.Utils.Validation;
import docSharing.UserDTO.UserDTO;
import docSharing.entities.User;
import docSharing.entities.VerificationToken;
import docSharing.repository.UserRepository;
import docSharing.service.AuthService;
import docSharing.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLDataException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    //we should make 2 tests for each function
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;
    private static final Gson gson = new Gson();

    private static Logger logger = LogManager.getLogger(AuthController.class.getName());


    //make these error check in other utils class.

    //here we should check if the user is null
    //check if the user.get.getEmail is null
    //check if user.getemail() is null
    //check if change user.getEmail() to-> user.getName()
    //same for password
    //put a log in the exception
    //what is HttpServletRequest?
    //
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody UserDTO user, HttpServletRequest request) {
        if (!Validation.isValidEmail(user.getEmail()) || user.getEmail()==null) {
            logger.error("In AuthenticationController.register: invalid email - int Level:200");
            return ResponseEntity.badRequest().body("Invalid email address!");
        }
        if (!Validation.isValidName(user.getName()) || user.getEmail()==null) {
            logger.error("In AuthenticationController.register: invalid name - int Level:200");
            return ResponseEntity.badRequest().body("Invalid name address!");
        }
        if (!Validation.isValidPassword(user.getPassword())|| user.getPassword()==null) {
            logger.error("In AuthenticationController.register: invalid password - int Level:200");
            return ResponseEntity.badRequest().body("Invalid password address!");
        }

        try {
            User createdUser =authService.createUser(user);
            String appUrl = request.getContextPath();
            authService.publishRegistrationEvent(createdUser, request.getLocale(), appUrl);
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(createdUser));
        } catch (SQLDataException e) {
            return ResponseEntity.badRequest().body("Email already exist");
        }
    }

    //here we should convert the RequestParams to -> request body
    //here we should check if the user is null and if newEmail is null

    @RequestMapping(value = "token", method = RequestMethod.PATCH)
    public ResponseEntity<String> updateTokenEmailKey (@RequestBody UserDTO user, @RequestParam String newEmail)
    {
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(authService.updateTokenEmailKey(user ,newEmail)));
    }

    //check validation in a good way
    //add logger

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<String> logIn(@RequestBody UserDTO user) {

       logger.info("in login");

        if (!Validation.isValidEmail(user.getEmail())|| user.getEmail()==null) { //TODO: edit validation funcitons
            return ResponseEntity.badRequest().body("Inalid email..");
        }
        if (!Validation.isValidPassword(user.getEmail())|| user.getPassword()==null) { //TODO: edit validation funcitons
            return ResponseEntity.badRequest().body("Inalid password..");
        }

        if (!authService.isEnabledUser(user))
            return ResponseEntity.badRequest().body("You need to confirm your email..");

        Optional<String> token =authService.login(user);
        if (!token.isPresent()){
            return ResponseEntity.badRequest().body("Wrong email or password..");
        }
    //here you should return a token and not the new login.
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(authService.login(user)));
    }

    //make the validation in an util class.
    // check if token is null
    //add logger

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(WebRequest request, @RequestParam("token") String token) {

        Locale locale = request.getLocale();

        VerificationToken verificationToken = authService.getVerificationToken(token);
        if (verificationToken == null) {
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        User user = verificationToken.getUser();
        //why you didn't do auto wire?
        Calendar cal = Calendar.getInstance();
        //make this if prettier.
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        userService.updateEnabled(user.getId(), true);
        authService.deleteVerificationToken(token);
        return "redirect:/login.html?lang=" + request.getLocale().getLanguage();
    }

}
