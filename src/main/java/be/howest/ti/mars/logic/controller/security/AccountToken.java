package be.howest.ti.mars.logic.controller.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Random;

public class AccountToken {
    private final byte[] token;

    public AccountToken() {
        this("");
    }

    public AccountToken(String username) { // 100% prevent duplicates, UUIDs are not 100% unique
        token = SecureHash.getUniqueHash(new Random(System.currentTimeMillis()).nextInt(5000) + username);   //makes PRNG "random", random int isn't really needed
    }

    @JsonCreator
    public AccountToken(@JsonProperty("token") byte[] token) {
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
        AccountToken that = (AccountToken) o;
        return Arrays.equals(token, that.token);
    }
}
