package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.exceptions.AuthenticationException;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.controller.security.AccountToken;
import be.howest.ti.mars.logic.data.MarsH2Repository;
import io.vertx.core.json.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class MarsController {
    private static final Logger LOGGER = Logger.getLogger(MarsController.class.getName());
    private static final String MOTD = "SmellyEllie";
    MarsH2Repository repo = new MarsH2Repository();
    Set<UserAccount> userAccounts = new HashSet<>();
    Set<BusinessAccount> businessAccounts = new HashSet<>();

    public String getMessage() {
        return MOTD;
    }

    public Set<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public Set<BusinessAccount> getBusinessAccounts() {
        return businessAccounts;
    }

    public void createAccount(String name, String password, String address, int endpoint, boolean isBusiness) {
        if (isBusiness) {
            BusinessAccount account = new BusinessAccount(name, password, endpoint, address);
            if (userAccounts.contains(new UserAccount(name)) || !businessAccounts.add(account)) { // username exists already
                throw new UsernameException("Username (" + name + ") is already taken");
            }
            repo.addBusiness(account);
        } else {
            UserAccount account = new UserAccount(name, password, endpoint, address);
            if (businessAccounts.contains(new BusinessAccount(name)) || !userAccounts.add(account)) { // username exists already
                throw new UsernameException("Username (" + name + ") is already taken");
            }
            repo.addUser(account);
        }
    }

    public void createDelivery(String deliveryType, int from, int destination, String date) {
        Date convertedDate;
        try {
            convertedDate = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            Delivery delivery = new Delivery(deliveryType, from, destination, convertedDate.toString());
            repo.addDelivery(delivery);
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    public byte[] login(String name, String password) {
        BaseAccount account = Stream.concat(
                userAccounts.stream(),
                businessAccounts.stream())
                .filter(acc -> acc.getUsername().equalsIgnoreCase(name) && acc.getPassword().equals(password))
                .findAny().orElse(null);

        if (account == null) {   // pw and name doesnt match
            throw new AuthenticationException("Credentials does not match!");
        } else {
            account.setAccountToken(new AccountToken(name)); // sets a new token, invalidates previous set token
            return account.getAccountToken().getToken();
        }
    }

    public void logout(BaseAccount account) {
        account.setAccountToken(null);
    }

    public List<Subscription> getSubscriptions() {
        return repo.getSubscriptions();
    }

    public Object addFriend(UserAccount user, String friendName) { // TODO: 20-11-2020 validation friend exists and not already friended
        if (userAccounts.contains(new UserAccount(friendName))) {
            user.addFriend(friendName);
        } else {
            //throw error
        }
        return "You just added a friend called:" + friendName;
    }

    public Object removeFriend(UserAccount user, String friendName) { // TODO: 20-11-2020 validation friend exists and not friended
        if (userAccounts.contains(new UserAccount(friendName))) {
            user.removeFriend(friendName);
        } else {
            //throw error
        }
        return "You just removed a friend called:" + friendName;
    }

    public Object buySubscription(BaseAccount acc, String subscriptionName, boolean userAcc) {
        repo.buySubscription(acc, subscriptionName, userAcc);
        return "Thank you for buying a subscription.";
    }

    public Object stopSubscription(BaseAccount acc, boolean userAcc) {
        repo.stopSubscription(acc, userAcc);
        return "We are sorry for you to stop you current subscription.";
    }

    public Object viewSubscriptionInfo(BusinessAccount businessAccount) {
        return repo.getSubscription(businessAccount, false);
    }

    public MarsH2Repository getRepo() {
        return repo;
    }

    public void favoriteEndpoint(BaseAccount acc, int id) {
        repo.favoriteEndpoint(acc, id);
    }

    public void unFavoriteEndpoint(BaseAccount acc, int id) {
        repo.unFavoriteEndpoint(acc, id);
    }

    public Object getAccountInformation(BaseAccount acc, boolean userAcc) { // TODO: 20-11-2020 missing shareLocation
        JsonObject accInformation = new JsonObject();
        accInformation.put("name:", acc.getUsername());
        accInformation.put("homeAddress:", acc.getAddress());
        accInformation.put("homeEndpoint:", acc.getHomeAddressEndpoint());
        accInformation.put("favouriteEndpoints:", repo.getFavoriteEndpoints(acc));

        if (userAcc) {
          //  Subscription sub = repo.getSubscription(acc, true);
           // accInformation.put("subscription:", sub != null ? sub.getName() : "No subscription");
            accInformation.put("friends:", repo.getFriends((UserAccount) acc, userAccounts));
            accInformation.put("travelHistory:", repo.getTravelHistory((UserAccount) acc));

        } else {
          //  Subscription sub = repo.getSubscription(acc, false);
           // accInformation.put("subscription:", sub != null ? sub.getName() : "No subscription");
        }

        return accInformation;
    }

    public void travel(UserAccount acc, int from, int destination, String type) { // getShortEndpoint also validates if endpoint exists
        repo.travel(acc, new Travel(repo.getShortEndpoint(from), repo.getShortEndpoint(destination), PodType.valueOf(type), ""));
    }

    public Object getTravelHistory(UserAccount acc) {
        return repo.getTravelHistory(acc);
    }
}
