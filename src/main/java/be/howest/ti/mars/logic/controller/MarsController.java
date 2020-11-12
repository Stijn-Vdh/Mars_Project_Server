package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.exceptions.UsernameIsTakenException;

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
            throw new UsernameIsTakenException(name + " is already taken");
        }
        return account.getUserToken().getToken();
    }
}
