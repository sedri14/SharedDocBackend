package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import docSharing.DTO.User.UserDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "User")
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "FieldHandler"})
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

    @Column(name = "enabled")
    private boolean enabled;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<Document> myDocs;   //my owned docs

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private Set<Permission> permissions = new HashSet<>(); //docs I have permission (viewer/editor).

    @OneToMany(cascade = CascadeType.ALL)
    private Set<INode> sharedWithMe;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Log> log = new HashSet<>();

    User() {

    }

    private User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = false;
    }

    public static User createNewUserFromUserDTO(UserDTO userDTO) {
        return new User(userDTO.name, userDTO.email, userDTO.password);
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    //    public void setMyDocs(List<Path> myDocs) {
//        this.myDocs = myDocs;
//    }


    //change the hashcode and the equal to newer one.

    @Override
    public String toString() {
        return "User: id=" + id + ", name='" + name + ", email='" + email + ", password='" + password;
    }

    public Set<Document> getMyDocs() {
        return myDocs;
    }
}
