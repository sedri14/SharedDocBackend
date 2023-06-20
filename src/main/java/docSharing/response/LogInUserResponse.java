package docSharing.response;

public class LogInUserResponse {

    String token;

    String email;

    Long rootId;


    public LogInUserResponse(String token, String email, Long rootId) {
        this.token = token;
        this.email = email;
        this.rootId = rootId;
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

    public Long getRootId() {
        return rootId;
    }
}


