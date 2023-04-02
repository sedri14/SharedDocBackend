package docSharing.CRDT;

import java.util.ArrayList;
import java.util.List;

public class Char {

    Character value;
    List<Identifier> position;


    private Char(Character value, List<Identifier> position) {
        this.position = position;
        this.value = value;
    }

    public static Char createNewChar(Character value, List<Identifier> position) {
        return new Char(value, position);
    }
}
