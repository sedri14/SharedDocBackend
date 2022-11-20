package docSharing.test;

public class ManipulatedText {
    private String user;
    private UpdateType type;
    private String content;
    private int startPosition;
    private int endPosition;

    public ManipulatedText() {
    }

    public ManipulatedText(String user, UpdateType type, String content, int startPosition, int endPosition) {
        this.user = user;
        this.type = type;
        this.content = content;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public String getUser() {
        return user;
    }

    public UpdateType getType() {
        return type;
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

    public void setUser(String user) {
        this.user = user;
    }

    public void setType(UpdateType type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    @Override
    public String toString() {
        return "ManipulatedText{" +
                "user='" + user + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                '}';
    }
}
