package docSharing.DTO.Doc;

public class CurrentViewingUserDTO {
    public String userName;

    public CurrentViewingUserDTO() {
    }

    public CurrentViewingUserDTO(String userName) {
        this.userName = userName;
    }



    @Override
    public String toString() {
        return "OnlineUser{" +
                "userName='" + userName + '\'' +
                '}';
    }
}