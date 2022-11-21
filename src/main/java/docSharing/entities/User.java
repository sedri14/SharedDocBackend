package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
//import java.nio.file.Path;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "User")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<Document> myDocs;   //my owned docs

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Permission> permissions = new HashSet<>(); //docs I have permission (viewer/editor).

    User() {

    }

    private User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static User createUserFactory(String name, String email, String password) {
        return new User(name, email, password);
    }

//    public void setId(Long id) {
//        this.id = id;
//    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public Set<Document> getDocs() {
//        return myDocs;
//    }

//    public void setMyDocs(List<Path> myDocs) {
//        this.myDocs = myDocs;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!Objects.equals(id, user.id)) return false;
        if (!Objects.equals(name, user.name)) return false;
        if (!Objects.equals(email, user.email)) return false;
        if (!Objects.equals(password, user.password)) return false;
        return Objects.equals(myDocs, user.myDocs);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (myDocs != null ? myDocs.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User: id=" + id + ", name='" + name + ", email='" + email + ", password='" + password;
    }

    public Set<Document> getMyDocs() {
        return myDocs;
    }
}
