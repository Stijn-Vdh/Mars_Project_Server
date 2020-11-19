package be.howest.ti.mars.logic.controller.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class SecureHash {    //according to internet PBKDF2 is more secure than SHA-512 since its slower to crack

    private SecureHash() {
    }

    public static byte[] getUniqueHash(String uid) {

        SecureRandom random = new SecureRandom(); // prevents collisions, if UID isn't unique
        byte[] salt = new byte[64];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(uid.toCharArray(), salt, 65536, 512);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Shouldn't be happening");
        }
    }

    public static byte[] getHash(String key) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            return factory.generateSecret(new PBEKeySpec(key.toCharArray(), new byte[64], 65536, 512)).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Shouldn't be happening");
        }
    }

    public static String getHashEncoded(String key) {
        return new String(getHash(key), StandardCharsets.UTF_8);
    }

}
