package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.exceptions.AuthenticationException;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.controller.security.AccountToken;
import be.howest.ti.mars.logic.data.MarsRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class MarsController {
    MarsRepository repo = new MarsRepository();
    Set<UserAccount> userAccounts = new HashSet<>();
    Set<BusinessAccount> businessAccounts = new HashSet<>();

    public String getMessage() {
        return "SmellyEllie";
    }

    public Set<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public Set<BusinessAccount> getBusinessAccounts() {
        return businessAccounts;
    }

    public void createAccount(String name, String password, String address, int endpoint, boolean isBusiness) {
        if (isBusiness) {
            BusinessAccount account = new BusinessAccount(name, password, endpoint, address, null);
            if (userAccounts.contains(new UserAccount(name)) || !businessAccounts.add(account)) { // username exists already
                throw new UsernameException("Username (" + name + ") is already taken");
            }
            repo.addBusiness(account);
        } else {
            UserAccount account = new UserAccount(name, password, endpoint, address, null);
            if (businessAccounts.contains(new BusinessAccount(name)) || !userAccounts.add(account)) { // username exists already
                throw new UsernameException("Username (" + name + ") is already taken");
            }
            repo.addUser(account);
        }


    }

    public void createDelivery(String deliveryType, int from, int destination, String date) {
        Date convertedDate = null;
        try {
            convertedDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Delivery delivery = new Delivery(deliveryType, from, destination, date);
        repo.addDelivery(delivery);
    }


    public byte[] login(String name, String password) {
        BaseAccount account = Stream.concat(
                userAccounts.stream(),
                businessAccounts.stream())
                .filter(acc -> acc.getUsername().equals(name) && acc.getPassword().equals(password))
                .findAny().orElse(null);

        if (account == null) {   // pw and name doesnt match
            throw new AuthenticationException("Credentials does not match!");
        } else {
            account.setUserToken(new AccountToken(name)); // sets a new token, invalidates previous set token
            return account.getUserToken().getToken();
        }
    }

    public void logout(BaseAccount account) {
        account.setUserToken(null);
    }

    public Set<Subscription> getSubscriptions() {
        return repo.getSubscriptions();
    }

    public Object addFriend(UserAccount user, String friendName) {
        UserAccount friendAccount = userAccounts.stream()
                .filter(acc -> acc.getUsername().equals(friendName))
                .findAny().orElse(null);
        assert friendAccount != null;
        return user.addFriend(friendAccount).getUsername();
    }

    public Object removeFriend(UserAccount user, String friendName) {
        UserAccount friendAccount = userAccounts.stream()
                .filter(acc -> acc.getUsername().equals(friendName))
                .findAny().orElse(null);
        assert friendAccount != null;
        return user.removeFriend(friendAccount).getUsername();
    }

    public MarsRepository getRepo() {
        return repo;
    }
}
