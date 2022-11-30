package docSharing.response;

public class LoginObject {
    private final long id;
    private final String token;
    private final String msg;

    private LoginObject(long id, String token, String msg) {
        this.id = id;
        this.token = token;
        this.msg = msg;
    }

    public  static LoginObject createLoginObject(long id, String token, String msg){
        return new LoginObject(id, token,msg);
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
}
