package docSharing.DTO.Doc;

public class ManipulatedTextDTO {
    public Long userId;
    public UpdateTypeDTO action;
    public String content;
    public int startPosition;
    public int endPosition;

    public ManipulatedTextDTO() {
    }

    public ManipulatedTextDTO(Long userId, UpdateTypeDTO action, String content, int startPosition, int endPosition) {
        this.userId = userId;
        this.action = action;
        this.content = content;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public Long getUserId() {
        return userId;
    }

    public UpdateTypeDTO getAction() {
        return action;
    }

    public String getContent() {
        return content;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setUser(Long userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "ManipulatedText{" +
                "userId='" + userId + '\'' +
                ", type=" + action +
                ", content='" + content + '\'' +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                '}';
    }
}
