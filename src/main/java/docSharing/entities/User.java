package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import docSharing.DTO.User.UserDTO;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private INode rootDirectory;

    @Column(name = "site_id", nullable = false)
    private int siteId;

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

    public List<SharedRole> getSharedItems() {
        return sharedItems;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

//Todo:change the hashcode and the equal to newer one.

    @Override
    public String toString() {
        return "User: id=" + id + ", name='" + name + ", email='" + email + ", password='" + password;
    }
}
