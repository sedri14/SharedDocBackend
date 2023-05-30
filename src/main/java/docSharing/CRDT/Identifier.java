package docSharing.CRDT;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Identifier {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    int digit;
    int siteId;

    Identifier() {

    }

    private Identifier(int digit, int siteId) {
        this.digit = digit;
        this.siteId = siteId;
    }

    public static Identifier create(int digit, int siteId) {
        return new Identifier(digit, siteId);
    }

    public static int compare (Identifier i1, Identifier i2) {
        if (i1.digit < i2.digit) {
            return -1;
        } else if (i1.digit > i2.digit) {
            return 1;
        } else {
            return Integer.compare(i1.siteId, i2.siteId);
        }
    }

    @Override
    public String toString() {
        return "(" + digit + "," + siteId + ")";
    }
}
