package docSharing.controller;

import com.google.gson.Gson;


import docSharing.Utils.Validation;
import docSharing.UserDTO.UserDTO;
import docSharing.entities.User;
import docSharing.entities.VerificationToken;
import docSharing.repository.UserRepository;
import docSharing.response.Response;
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


@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;
    private static final Gson gson = new Gson();

    private static Logger logger = LogManager.getLogger(AuthController.class.getName());

    public AuthController() {
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody UserDTO user, HttpServletRequest request) {
        if (!Validation.isValidEmail(user.getEmail()) || user.getEmail() == null) {
            logger.error("In AuthenticationController.register: invalid email - int Level:200");
            return ResponseEntity.badRequest().body(Response.failure("Invalid email address!").getMessage());

        }
        if (!Validation.isValidName(user.getName()) || user.getEmail() == null) {
            logger.error("In AuthenticationController.register: invalid name - int Level:200");

            return ResponseEntity.badRequest().body(Response.failure("Invalid name!").getMessage());


            return ResponseEntity.badRequest().body("Invalid name address!");

        }

        }

        try {
            Response<UserDTO> registerUser = authService.createUser(user);
            UserDTO createdUser = registerUser.getData();
            if (createdUser != null) {
                //String appUrl = request.getContextPath();
//                authService.publishRegistrationEvent(createdUser, request.getLocale(), appUrl);
//                System.out.println("inside AuthController");
                return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(createdUser));
            }
            else
                return ResponseEntity.badRequest().body(Response.failure("Email already exist").getMessage());

        } catch (
                SQLDataException e) {
            return ResponseEntity.badRequest().body(Response.failure("Email already exist").getMessage());
        }

    }

    @RequestMapping(value = "token", method = RequestMethod.PATCH)
    public ResponseEntity<String> updateTokenEmailKey(@RequestBody UserDTO user, @RequestParam String newEmail) {
        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(authService.updateTokenEmailKey(user, newEmail)));
    }



    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<String> logIn(@RequestBody UserDTO user) {


        logger.info("in login");
        System.out.println("in login");


        Response<String> loginResponse = authService.login(user);
        if (!loginResponse.isSuccess() || loginResponse.getData() == null)
            return ResponseEntity.badRequest().body(loginResponse.getMessage());
        else {
            System.out.println("Token:  ");
            return ResponseEntity.status(HttpStatus.OK).body(loginResponse.getData());

        }

    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(WebRequest request, @RequestParam("token") String token) {

        Locale locale = request.getLocale();

        VerificationToken verificationToken = authService.getVerificationToken(token);
        if (verificationToken == null) {
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        userService.updateEnabled(user.getId(), true);
        authService.deleteVerificationToken(token);
        return "redirect:/login.html?lang=" + request.getLocale().getLanguage();
    }


}
