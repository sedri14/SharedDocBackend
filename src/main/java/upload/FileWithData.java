package upload;

import org.springframework.web.multipart.MultipartFile;

public class FileWithData {

    private Long parentInodeId;

    private Long userId;
    private MultipartFile file;

    public Long getParentInodeId() {
        return parentInodeId;
    }

    public void setParentInodeId(Long parentInodeId) {
        this.parentInodeId = parentInodeId;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
