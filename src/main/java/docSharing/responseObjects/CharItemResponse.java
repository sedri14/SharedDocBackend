package docSharing.responseObjects;

import docSharing.CRDT.CharItem;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CharItemResponse implements Serializable {

    List<IdentifierResponse> pos;

    char val;

    private CharItemResponse(List<IdentifierResponse> position, char val) {
        this.pos = position;
        this.val = val;
    }

    public static CharItemResponse fromCharItem (CharItem charItem) {
        char c = charItem.getValue();
        if (c == 0x00) {
            c = ' ';
        }
        return new CharItemResponse(charItem.getPosition().stream().map(IdentifierResponse::fromIdentifier).collect(Collectors.toList()), c);
    }
}
