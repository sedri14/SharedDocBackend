package docSharing.response;

import docSharing.UserDTO.UserDTO;

public class RegisterObject {

    private final  UserDTO user;
    private final String msg;

    private RegisterObject(UserDTO user, String msg) {
        this.user = user;
        this.msg = msg;
    }

    public static RegisterObject createRegisterObject (UserDTO user, String msg){
        return new RegisterObject(user, msg);
    }

    public UserDTO getUser() {
        return user;
    }

    public String getMsg() {
        return msg;
    }
}
