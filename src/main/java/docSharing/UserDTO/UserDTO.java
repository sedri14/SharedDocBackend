package docSharing.UserDTO;

public class UserDTO {
    public String name;
    public String email;
    public String password;

   @Override
    public String toString() {
        return "LoginUser{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
