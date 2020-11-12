package be.howest.ti.mars.logic.controller;

import java.util.HashSet;
import java.util.Set;

public class MarsController {
    Set<BaseAccount> accounts = new HashSet<>();

    public String getMessage() {
        return "Hello, Mars!";
    }

    public byte[] createUser(String name, String password, String endpoint, String address){
        BaseAccount account = new UserAccount(name, password, endpoint, address);

        if(!accounts.add(account)){ // username exists already
            //throw error
        }
        return account.getUserToken().getToken();
    }
}
