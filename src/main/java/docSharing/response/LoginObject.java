package docSharing.response;

public class LoginObject {
    private final long id;
    private final String token;

    private final String name;
    private final String msg;

    private LoginObject(long id, String token, String msg, String name) {
        this.id = id;
        this.token = token;
        this.msg = msg;
        this.name = name;
    }

    public  static LoginObject createLoginObject(long id, String token, String msg, String name) {
        return new LoginObject(id, token, msg, name);
    }

    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getMsg() {
        return msg;
    }

    public String getName() {
        return name;
    }


}
