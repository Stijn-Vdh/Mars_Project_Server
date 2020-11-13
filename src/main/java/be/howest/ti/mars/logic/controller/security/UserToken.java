package be.howest.ti.mars.logic.controller.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;

public class UserToken {
    private final byte[] token;

    public UserToken() {
        this("");
    }

    public UserToken(String username) { // 100% prevent duplicates, UUIDs are not 100% unique
        token = SecureHash.getUniqueHash(new Random(System.currentTimeMillis()).nextInt(5000) + username);   //makes PRNG "random", random int isn't really needed
    }

    @JsonCreator
    public UserToken(@JsonProperty("token") byte[] token) {
        this.token = token;
    }

    public byte[] getToken() {
        return token;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserToken that = (UserToken) o;
        return Arrays.equals(token, that.token);
    }
}
