package docSharing.CRDT;
import javax.persistence.*;
import java.util.List;

@Entity
public class PositionedChar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 1)
    private String value;
    @OneToMany
    List<Identifier> position;


    private PositionedChar(Character value, List<Identifier> position) {
        this.position = position;
        this.value = value.toString();
    }

    PositionedChar() {

    }

    public static PositionedChar createNewChar(Character value, List<Identifier> position) {
        return new PositionedChar(value, position);
    }

    public Character getValue() {
        return value.charAt(0);
    }

    @Override
    public String toString() {
        return "val:" + value + " " + position;
    }
}
