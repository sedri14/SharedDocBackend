package docSharing.controller;

import docSharing.DTO.*;
import docSharing.entities.INode;
import docSharing.service.AuthService;
import docSharing.service.FileSystemService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upload.FileWithData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/fs")
public class FileSystemController {

    @Autowired
    private FileSystemService fsService;

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<INode> addInode(@RequestBody AddINodeDTO addINodeDTO, @RequestHeader String token) {


        return ResponseEntity.ok(fsService.addInode(addINodeDTO));
    }

    @RequestMapping(value = "/rename", method = RequestMethod.PATCH)
    public ResponseEntity<INode> rename(@RequestBody RenameINodeDTO renameINodeDTO, @RequestHeader String token) {
        //validations

        return ResponseEntity.ok(fsService.renameInode(renameINodeDTO.id, renameINodeDTO.name));
    }

    @RequestMapping(value = "/level", method = RequestMethod.POST)
    public ResponseEntity<List<INode>> getChildren(@RequestBody INodeDTO inodeDTO, @RequestHeader("token") String token) {
        //validate parameters (legal id)

        //validate token (dirNavigate.token)

        return ResponseEntity.ok(fsService.getInodesInLevel(inodeDTO.id));
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public ResponseEntity<INode> move(@RequestBody MoveINodeDTO moveINodeDTO, @RequestHeader("token") String token) {
        //validate: check target folder is not child of source folder.
        //validate token (token)
        Long sourceId = moveINodeDTO.sourceId;
        Long targetId = moveINodeDTO.targetId;

        if (!fsService.isExist(sourceId) || !fsService.isExist(targetId)) {
            //can't find files to move.
        }

        if (!fsService.isDir(targetId)) {
            //destination to move must be a directory.
        }

        if (!fsService.isHierarchicallyLegalMove(sourceId, targetId)) {
            //can't move an ancestor directory to one of its descendants
        }

        return ResponseEntity.ok(fsService.move(sourceId, targetId));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<List<INode>> delete(@RequestBody INodeDTO inodeDTO, @RequestHeader("token") String token) {
        //validate parameters: inodeId exists, validate user is owner
        //validate token (token)

        return ResponseEntity.ok(fsService.removeById(inodeDTO.id));
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<INode>> findAll() {
        List<INode> all = fsService.findAll();
        System.out.println(all); //this is ok
        return ResponseEntity.ok(all);
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseEntity<INode> uploadFile(@ModelAttribute FileWithData fileWithData, ModelMap modelMap, @RequestHeader("token") String token) {

        Long parentId = fileWithData.getParentInodeId();
        Long userId = fileWithData.getUserId();
        MultipartFile file = fileWithData.getFile();

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!fileExtension.equals("txt")) {
            return ResponseEntity.badRequest().build(); //file type is not supported.
        }

        String content = null;
        try {
            content = new String(file.getBytes());
        } catch (IOException e) {
            return ResponseEntity.badRequest().build(); //can not parse file content.
        }

        return ResponseEntity.ok(fsService.uploadFile(FilenameUtils.removeExtension(file.getOriginalFilename()), content, parentId, userId));
    }

}
