package docSharing.test;

public class OnlineUser {
    private String userName;

    public OnlineUser() {
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "OnlineUser{" +
                "userName='" + userName + '\'' +
                '}';
    }
}