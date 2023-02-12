//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//
//import static docSharing.Utils.Validation.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//
//public class ValidationTest {
//
//    //---Name_Validation---------------------------------------------------------------------
//    @Test
//    void isValidName_correctName_valid(){
//       Boolean bool= isValidName("Nitzan");
//
//        assertEquals(true, bool);
//    }
//
//    @Test
//    void isValidName_IncorrectName_Error(){
//        Boolean bool= isValidName("123");
//
//        assertEquals(false, bool);
//    }
//
//    @Test
//    void isValidEmail_NullName_Error(){
//        Assertions.assertThrows(NullPointerException.class, () -> isValidName(null));
//    }
//    //---Email_Validation---------------------------------------------------------------------
//
//    @Test
//    void isValidEmail_correctEmail_valid(){
//        Boolean bool= isValidEmail("Nitzan@gmail.com");
//
//        assertEquals(true, bool);
//    }
//
//    @Test
//    void isValidEmail_IncorrecEmail_Error(){
//        Boolean bool= isValidEmail("123");
//
//        assertEquals(false, bool);
//    }
//
//    @Test
//    void isValidEmail_NullEmail_Exception(){
//        Assertions.assertThrows(NullPointerException.class, () -> isValidEmail(null));
//    }
//
//    //---Password_Validation---------------------------------------------------------------------
//
//    @Test
//    void isValidPassword_correctPassword_valid(){
//        Boolean bool= isValidPassword("A1b2c34");
//
//        assertEquals(true, bool);
//    }
//
//    @Test
//    void isValidPassword_IncorrecPassword_Error(){
//        Boolean bool= isValidPassword("123");
//
//        assertEquals(false, bool);
//    }
//
//    @Test
//    void isValidPassword_NullPassword_Exception(){
//        Assertions.assertThrows(NullPointerException.class, () -> isValidPassword(null));
//    }
//
//
//
//
//}
//
//
