package docSharing.controller;

import docSharing.DTO.DirNavigateDTO;
import docSharing.DTO.AddINodeDTO;
import docSharing.entities.INode;
import docSharing.service.FileSystemService;
import docSharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/fs")
public class FileSystemController {

    @Autowired
    private FileSystemService fsService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<INode> addInode(@RequestBody AddINodeDTO addINodeDTO, @RequestHeader String token) {
        //validation

        return ResponseEntity.ok(fsService.addInode(addINodeDTO));
    }
    @RequestMapping(value = "/id", method = RequestMethod.POST)
    public ResponseEntity<List<INode>> getChildren(@RequestHeader("token") String token, @RequestBody DirNavigateDTO dirNavigate){
        //validate parameters (legal id)
        //validate token (dirNavigate.token)

        return ResponseEntity.ok(fsService.getInodesInLevel(dirNavigate.id));
    }


}
