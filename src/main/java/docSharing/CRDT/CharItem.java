package docSharing.CRDT;

import lombok.Getter;
import javax.persistence.*;
import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Entity
public class CharItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<Identifier> position;
    char value;

    CharItem() {

    }

    private CharItem(List<Identifier> position, char value) {
        this.position = position;
        this.value = value;
    }

    public static CharItem NewPositionedChar(List<Identifier> position, char value) {
        return new CharItem(position, value);
    }
}
