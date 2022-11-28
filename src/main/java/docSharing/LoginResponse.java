package docSharing;


import com.fasterxml.jackson.annotation.JsonValue;

public class LoginResponse {

     public enum loginEnum {
         EMAIL_NOT_EXIST("Email is NOT exist.."),
         CONFIRM_EMAIL("You need to confirm your email.."),
         INVALID_PASSWORD("Invalid password"),
         CORRECT("");

         private String loginEnum;

         loginEnum(String loginEnum) {this.loginEnum = loginEnum;}

         public String getLoginEnum() {return loginEnum;}
     }

     private String token;
     private boolean error;
     private loginEnum msg;


    public LoginResponse(String token, boolean error, loginEnum msg) {
        this.token = token;
        this.error = error;
        this.msg = msg;
    }

    public static LoginResponse createLoginResponse(String token, boolean error, loginEnum msg)
    {
        return new LoginResponse(token,error, msg);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public loginEnum getMsg() {
        return msg;
    }

    public void setMsg(loginEnum msg) {
        this.msg = msg;
    }
}