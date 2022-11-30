package upload;

import org.springframework.web.multipart.MultipartFile;

public class FileWithData {

    private Long parentInodeId;

    private Long userId;
    private MultipartFile file;

    public FileWithData() {
    }

    public FileWithData(Long parentInodeId, Long userId, MultipartFile file) {
        this.parentInodeId = parentInodeId;
        this.userId = userId;
        this.file = file;
    }

    public Long getParentInodeId() {
        return parentInodeId;
    }

    public Long getUserId() {
        return userId;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setParentInodeId(Long parentInodeId) {
        this.parentInodeId = parentInodeId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
