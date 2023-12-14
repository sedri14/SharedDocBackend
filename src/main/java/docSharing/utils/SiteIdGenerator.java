package docSharing.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SiteIdGenerator {

    public static int generateSiteId(String email) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(email.getBytes());
            byte[] hashBytes = md.digest();

            return byteArrayToInt(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            return -1;
        }
    }

    private static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value = (value << 8) | (bytes[i] & 0xFF);
        }
        return value;
    }
}
