package docSharing.controller;

import com.google.gson.Gson;
import docSharing.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private AuthService authService;
    private static final Gson gson = new Gson();

    private static Logger logger = LogManager.getLogger(AuthController.class.getName());






//    @RequestMapping(value = "/register", method = RequestMethod.POST)
//    public ResponseEntity<String> createUser(@RequestBody User user) {
//        if (!Validation.isValidEmail(user.getEmail())) {
//            logger.error("In AuthenticationController.register: invalid email - int Level:200");
//            throw new IllegalArgumentException("Invalid email address!");
//        }
//        if (!Validation.isValidName(user.getName())) {
//            logger.error("In AuthenticationController.register: invalid name - int Level:200");
//            throw new IllegalArgumentException("Invalid name!");
//        }
//        if (!Validation.isValidPassword(user.getPassword())) {
//            logger.error("In AuthenticationController.register: invalid password - int Level:200");
//            throw new IllegalArgumentException("Invalid password!");
//        }
//        return ResponseEntity.ok(gson.toJson(authService.createUser(user.getName(), user.getEmail(), user.getPassword())));
//        //return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(authService.createUser(user.getName(), user.getEmail(), user.getPassword())));
//
//    }
//
//    @RequestMapping(value = "/login", method = RequestMethod.POST)//we should add header.
//    public ResponseEntity<String> logIn(@RequestBody User user , @RequestParam String password) {
//
//        logger.debug("in AuthenticationController.login() - int Level:500");
//
//        if (!Validation.isValidEmail(user.getEmail())) {
//            logger.error("In AuthenticationController.login: invalid email - int Level:200");
//            throw new IllegalArgumentException("Your email address is invalid!");
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(authService.login(user,password)));
//    }

}
