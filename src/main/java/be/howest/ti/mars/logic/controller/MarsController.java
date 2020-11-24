package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.AuthenticationException;
import be.howest.ti.mars.logic.controller.exceptions.EndpointException;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.controller.security.AccountToken;
import be.howest.ti.mars.logic.data.MarsH2Repository;
import io.vertx.core.json.JsonObject;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MarsController {
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

    public void createDelivery(String deliveryType, int from, int destination, String sender) {
        repo.addDelivery(new Delivery(DeliveryType.valueOf(deliveryType), repo.getShortEndpoint(from), repo.getShortEndpoint(destination), "", sender));
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

    public Object addFriend(UserAccount user, String friendName) { // TODO: 20-11-2020 validation friend exists and not already friended and user and friend not same
        if (userAccounts.contains(new UserAccount(friendName))) {
            user.addFriend(friendName);
        } else {
            //throw error
        }
        return "You just added a friend called:" + friendName;
    }

    public Object removeFriend(UserAccount user, String friendName) { // TODO: 20-11-2020 validation friend exists and not friended  and user and friend not same
        if (userAccounts.contains(new UserAccount(friendName))) {
            user.removeFriend(friendName);
        } else {
            //throw error
        }
        return "You just removed a friend called:" + friendName;
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

    private JsonObject getAccountInformation(BaseAccount acc) {
        JsonObject accInformation = new JsonObject();
        accInformation.put("name:", acc.getUsername());
        accInformation.put("homeAddress:", acc.getAddress());
        accInformation.put("homeEndpoint:", acc.getHomeAddressEndpoint());
        accInformation.put("favouriteEndpoints:", repo.getFavoriteEndpoints(acc));
        return accInformation;
    }

    public Object getUserAccountInformation(UserAccount account) {
        JsonObject accInformation = getAccountInformation(account);
        accInformation.put("shareLocation", account.isSharesLocation());
        accInformation.put("subscription:", repo.getUserSubscription(account));
        accInformation.put("friends:", repo.getFriends(account, userAccounts).stream().map(UserAccount::getUsername).collect(Collectors.toList()));
        accInformation.put("travelHistory:", repo.getTravelHistory(account));
        return accInformation;
    }

    public Object getBusinessAccountInformation(BusinessAccount business) {
        JsonObject accInformation = getAccountInformation(business);
        accInformation.put("subscription:", repo.getBusinessSubscription(business));
        accInformation.put("Current usage subscription", repo.getBusinessSubscriptionInfo(business));
        return accInformation;
    }

    public void travel(UserAccount acc, int from, int destination, String type) { // getShortEndpoint also validates if endpoint exists
        if (from == destination) throw new EndpointException("Destination and from are the same endpoint");
        repo.travel(acc, new Travel(repo.getShortEndpoint(from), repo.getShortEndpoint(destination), PodType.valueOf(type), ""));
    }

    public Object getTravelHistory(UserAccount acc) {
        return repo.getTravelHistory(acc);
    }

    public void cancelTrip(UserAccount acc, int id) {
        repo.cancelTravel(acc, id);
    }

    public void changeDisplayName(BaseAccount acc, String newDN) {
        repo.changeDisplayName(acc, newDN);
    }
}
