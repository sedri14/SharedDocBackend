package docSharing.requestObjects;

import docSharing.CRDT.Identifier;

import java.util.List;

public class UpdateTextDTO {

    public List<Identifier> p;
    public List<Identifier> q;
    public char ch;
    public String email;

    UpdateTextDTO() {

    }

    public UpdateTextDTO(List<Identifier> p, List<Identifier> q, char ch, String email) {
        this.p = p;
        this.q = q;
        this.ch = ch;
        this.email = email;
    }
}
