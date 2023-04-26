package docSharing.DTO.User;

public class LogInUserResponse {

    String token;

    String email;


    public LogInUserResponse(String token, String email) {
        this.token = token;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


