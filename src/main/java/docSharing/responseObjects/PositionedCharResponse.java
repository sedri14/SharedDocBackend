package docSharing.responseObjects;

import java.util.List;

public class PositionedCharResponse {

    char val;
    List<Integer> pos;

    public PositionedCharResponse(char val, List<Integer> pos) {
        this.val = val;
        this.pos = pos;
    }

    public char getVal() {
        return val;
    }

    public List<Integer> getPos() {
        return pos;
    }
}
