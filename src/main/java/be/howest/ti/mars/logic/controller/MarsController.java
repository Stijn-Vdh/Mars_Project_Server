package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.exceptions.AuthenticationException;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.controller.security.UserToken;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.HashSet;
import java.util.Set;

public class MarsController {
    Set<BaseAccount> accounts = new HashSet<>();

    public String getMessage() {
        return "Hello, Mars!";
    }

    public void createUser(String name, String password, String endpoint, String address) {
        BaseAccount account = new UserAccount(name, password, endpoint, address, null);

        if (!accounts.add(account)) { // username exists already
            throw new UsernameException("Username (" + name + ") is already taken");
        }
    }

    public byte[] login(String name, String password) {
        BaseAccount account = accounts.stream()
                .filter(acc -> acc.getUsername().equals(name) && acc.getPassword().equals(password))
                .findAny().orElse(null);

        if (account == null) {   // pw and name doesnt match
            throw new AuthenticationException("Credentials does not match!");
        } else {
            account.setUserToken(new UserToken()); // sets a new token, invalidates previous set token
            return account.getUserToken().getToken();
        }
    }

    public boolean verifyAccountToken(String token) {
        UserToken userToken = Json.decodeValue(new JsonObject().put("token", token).toString(), UserToken.class);
        return accounts.stream().anyMatch(acc -> userToken.equals(acc.getUserToken()));
    }
}
