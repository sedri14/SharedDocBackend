package docSharing.controller;

import docSharing.DTO.*;
import docSharing.entities.INode;
import docSharing.entities.INodeType;
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

    /**
     * Adds an inode
     *
     * @param addINodeDTO - contains: userId - id of owner user
     *                    parentId - id of parent inode
     *                    name - inode name
     *                    type - type of inode (DIR/FILE)
     * @param token
     * @return a new inode
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<INode> addInode(@RequestBody AddINodeDTO addINodeDTO, @RequestHeader String token) {


        return ResponseEntity.ok(fsService.addInode(addINodeDTO));
    }

    /**
     * Renames an inode
     *
     * @param renameINodeDTO contains: id - inode id
     *                       name - inode name
     * @param token
     * @return renamed inode
     */
    @RequestMapping(value = "/rename", method = RequestMethod.PATCH)
    public ResponseEntity<INode> rename(@RequestBody RenameINodeDTO renameINodeDTO, @RequestHeader String token) {

        return ResponseEntity.ok(fsService.renameInode(renameINodeDTO.id, renameINodeDTO.name));
    }

    /**
     * Returns all inodes that are direct descendants of an inode
     *
     * @param inodeDTO contains: id - inode id
     * @param token
     * @return a list of inodes
     */
    @RequestMapping(value = "/level", method = RequestMethod.POST)
    public ResponseEntity<List<INode>> getChildren(@RequestBody INodeDTO inodeDTO, @RequestHeader("token") String token) {

        return ResponseEntity.ok(fsService.getInodesInLevel(inodeDTO.id));
    }

    /**
     * Moves an inode to another inode of type DIR
     *
     * @param moveINodeDTO contains: sourceId - id of an inode that is going to be moved
     *                     targetId - id of an inode that is the new parent
     * @param token
     * @return inode with a new parent
     */
    @RequestMapping(value = "/move", method = RequestMethod.POST)
    public ResponseEntity<INode> move(@RequestBody MoveINodeDTO moveINodeDTO, @RequestHeader("token") String token) {
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

    /**
     * Deletes an inode and all of its descendants
     *
     * @param inodeDTO contains: id - inode id
     * @param token
     * @return list of inodes deleted
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<List<INode>> delete(@RequestBody INodeDTO inodeDTO, @RequestHeader("token") String token) {
        //validate parameters: inodeId exists, validate user is owner
        //validate token (token)

        return ResponseEntity.ok(fsService.removeById(inodeDTO.id));
    }

//    @RequestMapping(value = "/all", method = RequestMethod.GET)
//    public ResponseEntity<List<INode>> findAll() {
//        List<INode> all = fsService.findAll();
//        System.out.println(all); //this is ok
//        return ResponseEntity.ok(all);
//    }

    /**
     * @param fileWithData contains: parentInodeId - id of parent node
     *                     userId - id of owner user
     *                     file
     * @param token
     * @return a new document identical to the uploaded file
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseEntity<INode> uploadFile(@ModelAttribute FileWithData fileWithData, @RequestHeader("token") String token) {

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
