package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.exceptions.UsernameIsTakenException;
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
            throw new UsernameIsTakenException(name + " is already taken");
        }
        return account.getUserToken().getToken();
    }

    public byte[] login(String name, String password){
        BaseAccount account = accounts.stream()
                .filter(acc -> acc.getUsername().equals(name) && acc.getPassword().equals(password))
                .findAny().orElse(null);

       if (account == null){   //throw error
           throw new RuntimeException();

       }else{
           account.setUserToken(new UserToken()); // sets a new token, invalidates previous set tokens
           return account.getUserToken().getToken();
       }
    }
}
