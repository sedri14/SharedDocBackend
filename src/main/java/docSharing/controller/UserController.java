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

    //here you should not throw exception you should return an error or something.
    //check if things are null
    //why i should send all the user to update the name?

    //we cna send a String instead.
    @RequestMapping(value = "/name", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUserName(@RequestBody UserDTO user, @RequestHeader String token) throws IOException {
        if (!Validation.isValidName(user.getName())) {
            return ResponseEntity.badRequest().build();
        }
        validateToken(user.getEmail(), token);
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserName(user.getEmail(), user.getName()));
    }

    private void validateToken(String email, String token) throws IOException {
        if (!authService.isValidToken(email, token)) {
            throw new AccessDeniedException(String.format("User with email address: %s is not logged in!", email));
        }
    }


//    @RequestMapping(value = "/email", method = RequestMethod.PATCH)
//    public ResponseEntity<User> updateUserEmail(@RequestBody UserDTO user, @RequestHeader String token)  {
//        if (!Validation.isValidEmail(user.getEmail())) {return ResponseEntity.badRequest().build();}
//        try {
//            validateToken(user.getEmail(), token);
//        } catch (IOException e) {
//            ResponseEntity.badRequest(); //TODO: check how to wrap it with responser entity with user
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserEmail(user.id, user.getEmail()));
//    }

    //chante to requestBody ->
    //change the response to string.
    @RequestMapping(value = "/password", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUserPassword(@RequestBody User user, @RequestParam String password, @RequestHeader String token){
        if (!Validation.isValidPassword(password)) {
            return ResponseEntity.badRequest().build();
        }
        //validateToken(email, token);
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserPassword(user.getEmail(), password));
    }

//don't throw an exception here.
    //this should reutrn a string entity.
    //PathVariables use id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@RequestParam String email, @RequestHeader String token) throws IOException {
        validateToken(email, token);
        //delete user should return something.
        userService.deleteUser(email);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<User>> all(){
    //userservice.fidall could return null
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

}
