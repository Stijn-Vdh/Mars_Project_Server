package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.exceptions.AuthenticationException;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.controller.security.UserToken;

import java.util.HashSet;
import java.util.Set;

public class MarsController {
    Set<BaseAccount> accounts = new HashSet<>();

    public String getMessage() {
        return "Hello, Mars!";
    }

    public byte[] createUser(String name, String password, String endpoint, String address) {
        BaseAccount account = new UserAccount(name, password, endpoint, address);

        if (!accounts.add(account)) { // username exists already
            throw new UsernameException("Username (" + name + ") is already taken");
        }
        return account.getUserToken().getToken();
    }

    public byte[] login(String name, String password) {
        BaseAccount account = accounts.stream()
                .filter(acc -> acc.getUsername().equals(name) && acc.getPassword().equals(password))
                .findAny().orElse(null);

        if (account == null) {   // pw and name doesnt match
            throw new AuthenticationException("Credentials does not match!");
        } else {
            account.setUserToken(new UserToken()); // sets a new token, invalidates previous set tokens
            return account.getUserToken().getToken();
        }
    }
}
