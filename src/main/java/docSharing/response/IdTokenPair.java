package docSharing.response;

public class IdTokenPair {
    private long id;
    private String token;

    private IdTokenPair(long id, String token) {
        this.id = id;
        this.token = token;
    }

    public  static IdTokenPair createIdTokenPair(long id, String token){
        return new IdTokenPair(id, token);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
