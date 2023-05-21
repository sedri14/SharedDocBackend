package docSharing.CRDT;

import lombok.Getter;

import java.util.List;

@Getter
public class Char {
    List<Identifier> position;
    char value;

    Char() {

    }

    private Char(List<Identifier> position, char value) {
        this.position = position;
        this.value = value;
    }

    public static Char NewPositionedChar(List<Identifier> position, char value) {
        return new Char(position, value);
    }
}
