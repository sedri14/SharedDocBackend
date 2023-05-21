package docSharing.DTO;

import docSharing.CRDT.Identifier;

import java.util.List;

public class UpdateTextDTO {

    public List<Identifier> p;
    public List<Identifier> q;
    public char ch;

    public int siteId;

    UpdateTextDTO() {

    }

    public UpdateTextDTO(List<Identifier> p, List<Identifier> q, char ch) {
        this.p = p;
        this.q = q;
        this.ch = ch;
    }
}
