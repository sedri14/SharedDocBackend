package docSharing.response;

public enum LoginEnum {
    EMAIL_NOT_EXIST("Email is NOT exist.."),
    CONFIRM_EMAIL("You need to confirm your email.."),
    INVALID_PASSWORD("Invalid password"),
    CORRECT("");

    private String loginEnum;

    LoginEnum(String loginEnum) {this.loginEnum = loginEnum;}

    public String getLoginEnum() {return loginEnum;}
}


