package docSharing.requestObjects.responseObjects;

import docSharing.CRDT.Identifier;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class IdentifierResponse implements Serializable{

    int digit;

    int siteId;

    private IdentifierResponse(int digit, int siteId) {
        this.digit = digit;
        this.siteId = siteId;
    }

    public static IdentifierResponse fromIdentifier(Identifier identifier) {
        return new IdentifierResponse(identifier.getDigit(), identifier.getSiteId());
    }
}
