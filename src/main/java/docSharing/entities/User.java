package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import docSharing.DTO.User.UserDTO;

import javax.persistence.*;
import javax.print.Doc;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
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

    @OneToOne(cascade = CascadeType.ALL)
    private INode rootDirectory;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<SharedRole> sharedItems;

    User() {

    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static User createNewUserFromUserDTO(UserDTO userDTO) {
        return new User(userDTO.name, userDTO.email, userDTO.password);
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

    public INode getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(INode rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    //Todo:change the hashcode and the equal to newer one.

    @Override
    public String toString() {
        return "User: id=" + id + ", name='" + name + ", email='" + email + ", password='" + password;
    }
}
