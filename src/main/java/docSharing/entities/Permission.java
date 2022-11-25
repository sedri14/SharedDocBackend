package docSharing.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doc_id")
    private Document document;

    private UserRole userRole;


    Permission() {

    }

    public Permission(User user, Document document, UserRole userRole) {
        this.user = user;
        this.document = document;
        this.userRole = userRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;

        Permission that = (Permission) o;

        if (id != that.id) return false;
        if (!Objects.equals(user, that.user)) return false;
        if (!Objects.equals(document, that.document)) return false;
        return userRole == that.userRole;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (document != null ? document.hashCode() : 0);
        result = 31 * result + (userRole != null ? userRole.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Document getDocument() {
        return document;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
