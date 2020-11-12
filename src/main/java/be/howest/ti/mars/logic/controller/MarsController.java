package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.exceptions.AuthenticationException;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.controller.security.UserToken;
import be.howest.ti.mars.logic.data.MarsRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MarsController {
    Set<BaseAccount> accounts = new HashSet<>();
    MarsRepository repo = new MarsRepository();

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

    public Set<Subscription> getSubscriptions(){
        return repo.getSubscriptions();
    }
}
