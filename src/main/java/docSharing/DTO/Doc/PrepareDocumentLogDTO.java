package docSharing.DTO.Doc;

import java.util.ArrayList;
import java.util.List;

public class PrepareDocumentLogDTO {

    List<String> content;
    List<Integer> index;
    List<String> Action;
    List<Long> UserId;

    public PrepareDocumentLogDTO() {
        content = new ArrayList<>();
        index = new ArrayList<>();
        Action = new ArrayList<>();
        UserId = new ArrayList<>();
    }

    public List<String> getContent() {
        return content;
    }

    public List<String> getAction() {
        return Action;
    }

    public List<Integer> getIndex() {
        return index;
    }

    public List<Long> getUserId() {
        return UserId;
    }

    @Override
    public String toString() {
        return "PrepareDocumentLog{" +
                "content=" + content +
                ", index=" + index +
                ", Action=" + Action +
                ", UserId=" + UserId +
                '}';
    }
}
