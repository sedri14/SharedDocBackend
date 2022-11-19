package docSharing.controller;

import docSharing.DTO.DirNavigateDTO;
import docSharing.DTO.AddINodeDTO;
import docSharing.entities.INode;
import docSharing.service.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/dir")
public class FileSystemController {

    @Autowired
    private FileSystemService fsService;

    //TODO- this doesn't work
    @RequestMapping(value = "/id", method = RequestMethod.POST)
    public ResponseEntity<List<INode>> getInodesInLevel(@RequestHeader("token") String token, @RequestBody DirNavigateDTO dirNavigate){
        //validate parameters (legal id)
        //validate token (dirNavigate.token)

        return ResponseEntity.ok(fsService.getInodesInLevel(dirNavigate.id));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<INode> addInode(@RequestBody AddINodeDTO addINodeDTO) {
        //validation

        return ResponseEntity.ok(fsService.addInode(addINodeDTO));
    }

}
