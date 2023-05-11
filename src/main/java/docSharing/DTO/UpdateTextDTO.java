package docSharing.DTO;

import java.util.List;

public class UpdateTextDTO {

    public List<Integer> p;
    public List<Integer> q;
    public char ch;

    UpdateTextDTO() {

    }

    public UpdateTextDTO(List<Integer> p, List<Integer> q, char ch) {
        this.p = p;
        this.q = q;
        this.ch = ch;
    }
}
