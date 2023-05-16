package docSharing.CRDT;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class PositionedChar implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 1)
    private String value;
    @OneToMany(cascade = CascadeType.PERSIST)
    @ElementCollection
    List<Identifier> position;

    //Site id here
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

    public List<Identifier> getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "val:" + value + " pos: " + position + "\n";
    }
}
