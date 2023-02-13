package docSharing.controllers;

import com.google.gson.Gson;
import docSharing.DTO.User.UserDTO;
import docSharing.Utils.Validation;
import docSharing.entities.User;
import docSharing.entities.VerificationToken;
import docSharing.exceptions.InvalidFormatException;
import docSharing.repository.UserRepository;
import docSharing.response.LoginObject;
import docSharing.response.RegisterObject;
import docSharing.response.Response;
import docSharing.service.AuthService;
import docSharing.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private UserRepository userRepository;
    private static final Gson gson = new Gson();

    private static Logger logger = LogManager.getLogger(AuthController.class.getName());

    public AuthController() {
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<User> register(@RequestBody UserDTO userDTO) {
        if (!Validation.isValidEmail(userDTO.getEmail()) || userDTO.getEmail() == null) {
            throw new InvalidFormatException("email");

        }
        if (!Validation.isValidName(userDTO.getName()) || userDTO.getEmail() == null) {
            throw new InvalidFormatException("name");

        }
        if (!Validation.isValidPassword(userDTO.getPassword()) || userDTO.getPassword() == null) {
            throw new InvalidFormatException("password");
        }

        return ResponseEntity.ok(authService.register(userDTO));
    }
//
//    @RequestMapping(value = "token", method = RequestMethod.PATCH)
//    public ResponseEntity<String> updateTokenEmailKey(@RequestBody UserDTO user, @RequestParam String newEmail) {
//        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(authService.updateTokenEmailKey(user, newEmail)));
//    }


    @RequestMapping(value = "login", method = RequestMethod.POST)//
    public ResponseEntity<Response<LoginObject>> login(@RequestBody UserDTO user) {

        logger.info("in login");
        System.out.println("in login");

        LoginObject loginObject = authService.login(user);

        if (loginObject.getMsg() != null) //error happened
            return ResponseEntity.badRequest().body(Response.failure(loginObject.getMsg()));
        else {
            System.out.println("Token:  ");
            return ResponseEntity.status(HttpStatus.OK).body(Response.success(loginObject));

        }
    }
//
//
//    @GetMapping("/registrationConfirm")
//    public String confirmRegistration(WebRequest request, @RequestParam("token") String token) {
//
//        Locale locale = request.getLocale();
//
//        VerificationToken verificationToken = authService.getVerificationToken(token);
//        if (verificationToken == null) {
//            return "redirect:/badUser.html?lang=" + locale.getLanguage();
//        }
//
//        User user = verificationToken.getUser();
//        Calendar cal = Calendar.getInstance();
//        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
//            return "redirect:/badUser.html?lang=" + locale.getLanguage();
//        }
//
//        userService.updateEnabled(user.getId(), true);
//        authService.deleteVerificationToken(token);
//        return "redirect:/login.html?lang=" + request.getLocale().getLanguage();
//    }


}
