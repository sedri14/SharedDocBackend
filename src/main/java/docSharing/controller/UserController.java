package docSharing.controller;

import docSharing.UserDTO.UserDTO;
import docSharing.Utils.Validation;
import docSharing.entities.User;
import docSharing.service.AuthService;
import docSharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;


    /**
     *Method updates user's name
     * @param token
     * @return User in case of success OR Error
     */

    @RequestMapping(value = "/name", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUserName(@RequestBody UserDTO user, @RequestHeader String token) throws IOException {
        if (!Validation.isValidName(user.name)) {
            return ResponseEntity.badRequest().build();
        }
        validateToken(user.email, token);
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserName(user.email, user.name));
    }

    private void validateToken(String email, String token) throws IOException {
        if (!authService.isValidToken(email, token)) {
            throw new AccessDeniedException(String.format("User with email address: %s is not logged in!", email));
        }
    }


    @RequestMapping(value = "/email", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUserEmail(@RequestBody UserDTO user, @RequestHeader String token)  {
        if (!Validation.isValidEmail(user.email)) {return ResponseEntity.badRequest().build();}
        try {
            validateToken(user.email, token);
        } catch (IOException e) {
            ResponseEntity.badRequest(); //TODO: check how to wrap it with responser entity with user
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserEmail(user.id, user.email));
    }


    @RequestMapping(value = "/password", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUserPassword(@RequestBody User user, @RequestParam String password, @RequestHeader String token){
        if (!Validation.isValidPassword(password)) {
            return ResponseEntity.badRequest().build();
        }
        //validateToken(email, token);
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserPassword(user.getEmail(), password));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@RequestParam String email, @RequestHeader String token) throws IOException {
        validateToken(email, token);
        userService.deleteUser(email);

        return ResponseEntity.noContent().build();
    }


    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<User>> all(){

        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

}
