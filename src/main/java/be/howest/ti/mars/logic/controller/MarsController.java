package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.exceptions.AuthenticationException;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.controller.security.UserToken;
import be.howest.ti.mars.logic.data.MarsRepository;

import java.util.HashSet;
import java.util.Set;

public class MarsController {
    MarsRepository repo = new MarsRepository();
    Set<BaseAccount> accounts = new HashSet<>();

    public String getMessage() {
        return "SmellyEllie";
    }

    public Set<BaseAccount> getAccounts() {
        return accounts;
    }

    public void createAccount(String name, String password, String address, int endpoint, boolean isBusiness) {
        BaseAccount account;
        if (isBusiness) {
            account = new BusinessAccount(name, password, endpoint, address, null);
        } else {
            account = new UserAccount(name, password, endpoint, address, null);
            UserAccount user = (UserAccount) account;
            repo.addUser(user);
        }

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
            account.setUserToken(new UserToken(name)); // sets a new token, invalidates previous set token
            return account.getUserToken().getToken();
        }
    }

    public void logout(BaseAccount account) {
        account.setUserToken(null);
    }

    public Set<Subscription> getSubscriptions() {
        return repo.getSubscriptions();
    }

    public Object addFriend(UserAccount user,String friendName) {
        UserAccount friendAccount = (UserAccount) accounts.stream()
                                                        .filter(acc -> acc.getUsername().equals(friendName))
                                                        .findAny().orElse(null);
        System.out.println(friendAccount);
        return user.addFriend(friendAccount);
    }

}
