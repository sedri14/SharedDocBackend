package docSharing.controllers;

import docSharing.DTO.User.UserDTO;
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
     * Method updates user's name
     *
     * @param token
     * @return User in case of success OR Error
     */

    @RequestMapping(value = "/name", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUserName(@RequestBody UserDTO user, @RequestHeader String token) throws IOException {
        if (!Validation.isValidName(user.getName())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserName(user.getEmail(), user.getName()));
    }

    @RequestMapping(value = "/password", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUserPassword(@RequestBody User user, @RequestParam String password, @RequestHeader String token) {
        if (!Validation.isValidPassword(password)) {
            return ResponseEntity.badRequest().build();
        }
        //validateToken(email, token);
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserPassword(user.getEmail(), password));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@RequestParam String email, @RequestHeader String token) throws IOException {
//        validateToken(email, token);
        //delete user should return something.
        userService.deleteUser(email);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<User>> all() {

        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
    }

}
