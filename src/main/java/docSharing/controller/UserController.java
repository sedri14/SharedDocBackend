package docSharing.controller;

import docSharing.UserDTO.UserDTO;
import docSharing.Utils.Validation;
import docSharing.entities.User;
import docSharing.service.AuthService;
import docSharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

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


    public ResponseEntity<String> deleteUser(@RequestBody UserDTO user, @PathVariable String id, @RequestHeader String token) throws IOException {
        validateToken(user.getEmail(), token);
        userService.deleteUser(user.getEmail());


        return ResponseEntity.status(HttpStatus.OK).body("User deleted");
    }


    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<User>> all(){
    //userservice.fidall could return null
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

}
