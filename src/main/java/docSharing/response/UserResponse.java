package docSharing.response;

import docSharing.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    String email;
    String name;

    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getEmail(), user.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserResponse)) return false;

        UserResponse that = (UserResponse) o;

        if (!Objects.equals(email, that.email)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
