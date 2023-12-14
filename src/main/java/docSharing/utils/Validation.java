package docSharing.utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.regex.Pattern;

public class Validation {
    private static final Logger logger = LogManager.getLogger(Validation.class.getName());

    public static boolean isValidPassword(String password) {
        return true;
        //return password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}");
    }

    public static boolean isValidName(String Name) {
        return Name.matches("^[ A-Za-z]+$");
    }

    public static boolean isValidEmail(String emailAddress) {
        String regexPattern = "^(.+)@(\\S+)$";

        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
