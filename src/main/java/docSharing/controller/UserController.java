package docSharing.controller;

import docSharing.Utils.Validation;
import docSharing.entities.User;
import docSharing.service.AuthService;
import docSharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "name", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUserName(@RequestParam String email, @RequestParam String name, @RequestHeader String token){
        if (!Validation.isValidName(name)) {
            return ResponseEntity.badRequest().build();
        }
        //validateToken(email, token);
        return ResponseEntity.ok(userService.updateUserName(email, name));
    }


    @RequestMapping(value = "email", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUserEmail(@RequestParam String email, @RequestParam String newEmail, @RequestHeader String token){
        if (!Validation.isValidEmail(newEmail)) {
            return ResponseEntity.badRequest().build();
        }
        //validateToken(email, token);
        return ResponseEntity.ok(userService.updateUserEmail(email, newEmail));
    }

    @RequestMapping(value = "password", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUserPassword(@RequestParam String email, @RequestParam String password, @RequestHeader String token){
        if (!Validation.isValidPassword(password)) {
            return ResponseEntity.badRequest().build();
        }
        //validateToken(email, token);
        return ResponseEntity.ok(userService.updateUserEmail(email, password));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserByEmail(@PathVariable("email") String email){
        //validateToken(email,token);
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }



    @RequestMapping(method = RequestMethod.GET)

    public ResponseEntity<User> getUserById(@RequestParam int id) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") int id) {
        return ResponseEntity.noContent().build();
    }
}
