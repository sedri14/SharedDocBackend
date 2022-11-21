package docSharing.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UsersList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @ElementCollection
    private final List<User> users;

    public UsersList() {
        this.users = new ArrayList<>();
    }

    public void add(User user) {
        this.users.add(user);
    }

    public void remove(User user) {
        this.users.remove(user);
    }

    public boolean contains(User user) {
        return this.users.contains(user);
    }
}