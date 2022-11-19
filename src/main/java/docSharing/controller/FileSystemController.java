package docSharing.controller;

import docSharing.Utils.Validation;
import docSharing.entities.User;
import docSharing.entities.abstracts.INode;
import docSharing.service.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class FileSystemController {

    @Autowired
    private FileSystemService fsService;

    @RequestMapping(value = "/dir", method = RequestMethod.POST)
    public ResponseEntity<List<INode>> getInodesInLevel(@RequestParam Long id){
        //validate parameter
        //validate token

        return ResponseEntity.ok(fsService.getInodesInLevel(id));
    }

//    @RequestMapping(value = "/name", method = RequestMethod.PATCH)
//    public ResponseEntity<User> updateUserName(@RequestParam String email, @RequestParam String name, @RequestHeader String token){
//        if (!Validation.isValidName(name)) {
//            return ResponseEntity.badRequest().build();
//        }
//        //validateToken(email, token);
//        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserName(email, name));
//    }
}
