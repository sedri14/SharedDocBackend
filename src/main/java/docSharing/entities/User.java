package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import docSharing.UserDTO.UserDTO;

import javax.persistence.*;
//import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;

    private UserRole userRole;

    @Column(name = "enabled")
    private boolean enabled;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<Document> myDocs;

    public User() {}

    private User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled=false;
    }

    public static User createUserFactory(String name, String email, String password)
    {
        return new User(name,email,password);
    }
    public static User createUserFactory(UserDTO other)
    {
        return new User(other.getName(), other.getEmail(), other.getPassword());
    }


    public void setId(Long id) {
        this.id = id;
    }

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

    public Set<Document> getDocs() {
        return myDocs;
    }

    public boolean isEnabled() {return enabled;}

    public void setEnabled(boolean enabled) {this.enabled = enabled;}

    //    public void setMyDocs(List<Path> myDocs) {
//        this.myDocs = myDocs;
//    }


@Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (!Objects.equals(name, user.name)) return false;
        if (!Objects.equals(email, user.email)) return false;
        return Objects.equals(password, user.password);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, userRole);

    }

    @Override
    public String toString() {return "User: id=" + id + ", name='" + name + ", email='" + email + ", password='" + password; }

}
