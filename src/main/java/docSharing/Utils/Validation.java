package docSharing.Utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

public class Validation {
    private static Logger logger = LogManager.getLogger(Validation.class.getName());

    public static boolean isValidPassword(String password) {
        logger.debug("in AuthenticationController.isValidPassword() - int Level:500");

        //todo: change this, just for test
        return true;
        //return password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}");
    }

    public static boolean isValidName(String Name) {
        logger.debug("in AuthenticationController.isValidName() - int Level:500");

        return Name.matches("^[ A-Za-z]+$");
    }

    public static boolean isValidEmail(String emailAddress) {
        logger.debug("in AuthenticationController.isValidEmail() - int Level:500");

        String regexPattern = "^(.+)@(\\S+)$";

        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    /**
     *
     * @param obj
     * @param <T>
     */
    public static <T> void nullCheck(T obj) {
        if (obj == null) {
            logger.error("param is null");
            throw new IllegalArgumentException("the parameter is null");
        }
    }


}
