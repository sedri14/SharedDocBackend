package docSharing.DTO.Doc;

public class UpdateDocContentRes {
    public Long userId;
    public String documentText;
    public int startPosition;
    public int endPosition;
    public UpdateTypeDTO updateTypeDTO;

    public UpdateDocContentRes() {
    }

    public UpdateDocContentRes(Long userId, String documentText, int startPosition, int endPosition, UpdateTypeDTO updateTypeDTO) {
        this.userId = userId;
        this.documentText = documentText;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.updateTypeDTO = updateTypeDTO;

    }

    @Override
    public String toString() {
        return "UpdateDocContentRes{" +
                "userId=" + userId +
                ", documentText='" + documentText + '\'' +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", updateTypeDTO=" + updateTypeDTO +
                '}';
    }
}
