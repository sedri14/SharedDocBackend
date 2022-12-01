package docSharing.DTO.FS;



public class PermissionDTO {

    public Long userId;

    public Long docId;


    public PermissionDTO() {
    }

    public PermissionDTO(Long userId, Long docId) {
        this.userId = userId;
        this.docId = docId;

    }
}