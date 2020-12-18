package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.enums.DeliveryType;
import be.howest.ti.mars.logic.controller.enums.PodType;
import be.howest.ti.mars.logic.controller.exceptions.AuthenticationException;
import be.howest.ti.mars.logic.controller.exceptions.EndpointException;
import be.howest.ti.mars.logic.controller.exceptions.MarsIllegalArgumentException;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.data.Repositories;
import io.vertx.core.json.JsonObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MTTSController extends AuthController {
    private static final String MOTD = "SmellyEllie";

    public String getMessage() {
        return MOTD;
    }

    public int sendPackage(DeliveryType deliveryType, int from, int destination, BaseAccount acc, boolean userAcc) {
        if (from == destination) {
            throw new AuthenticationException("!You cannot use the same endpoint as destination and from!");
        }
        if (deliveryType == DeliveryType.LARGE && userAcc) {
            throw new AuthenticationException("!Only businesses can send large package pods!");
        }
        if (!userAcc) {
            Repositories.getSubscriptionRepo().updateBusinessSubscription(deliveryType.equals(DeliveryType.LARGE), (BusinessAccount) acc);
        }
        return Repositories.getDeliveriesRepo().addDelivery(new Delivery(0, deliveryType, Repositories.getEndpointsRepo().getShortEndpoint(from), Repositories.getEndpointsRepo().getShortEndpoint(destination), "", acc.getUsername()));
    }

    public Object addFriend(UserAccount user, String friendName) {
        UserAccount potentialFriend = findUserByName(friendName);
        if (friendValidation(user, friendName, false)) {
            if (Repositories.getFriendsRepo().getFriends(user, true).contains(potentialFriend)) {
                user.removePotentialFriend(potentialFriend);
            } else {
                potentialFriend.addPotentialFriend(user);
            }
            user.addFriend(friendName);
        } else {
            throw new UsernameException("Could not add a friend with the given username or you are already friends with this person.");
        }
        return "You just added a friend called:" + friendName;
    }

    public UserAccount findUserByName(String friendName) {
        return Repositories.getAccountsRepo().getUserAccounts().stream()
                .filter(user -> user.equals(new UserAccount(friendName)))
                .findAny()
                .orElseThrow(() -> new MarsIllegalArgumentException("Friend with name(" + friendName + ") doesn't exist!"));
    }

    //controller stores the token not the DB, if more time than maybe it gets refactored
    public UserAccount findUserByNameController(String friendName) {
        return userAccounts.stream().filter(user -> user.equals(new UserAccount(friendName))).findAny().orElseThrow(() -> new MarsIllegalArgumentException("User not found."));
    }


    private boolean friendValidation(UserAccount acc, String friendName, boolean notFriended) { // cant friend yourself or companies or someone that you already (un)friended
        UserAccount friend = findUserByName(friendName);
        boolean exists = Repositories.getAccountsRepo().getUserAccounts().contains(friend);
        boolean notYourself = !acc.equals(friend);
        boolean isFriend = Repositories.getFriendsRepo().getFriends(acc, false).contains(friend);
        boolean isPotentialFriend = Repositories.getFriendsRepo().getFriends(acc, true).contains(friend);
        return exists && notYourself && notFriended == isFriend || isPotentialFriend;
    }

    public Object removeFriend(UserAccount user, String friendName) {
        UserAccount friend = findUserByName(friendName);
        if (friendValidation(user, friendName, true)) {
            if (Repositories.getFriendsRepo().getFriends(user, true).contains(friend)) {
                user.removePotentialFriend(friend);
            } else {
                user.removeFriend(friendName);
            }
            friend.removeFriend(user.getUsername());

        } else {
            throw new UsernameException("Could not remove a friend with the given username");
        }
        return "You just removed a friend called:" + friendName;
    }

    public void favoriteEndpoint(BaseAccount acc, int id) {
        Repositories.getFavoritesRepo().favoriteEndpoint(acc, id);
    }

    public void unFavoriteEndpoint(BaseAccount acc, int id) {
        Repositories.getFavoritesRepo().unFavoriteEndpoint(acc, id);
    }

    private JsonObject getAccountInformation(BaseAccount acc) {
        JsonObject accInformation = new JsonObject();
        accInformation.put("name", acc.getUsername());
        accInformation.put("homeAddress", acc.getAddress());
        accInformation.put("homeEndpoint", acc.getHomeEndpoint());
        accInformation.put("favouriteEndpoints", Repositories.getFavoritesRepo().getFavoriteEndpoints(acc));
        return accInformation;
    }

    public Object getUserAccountInformation(UserAccount account) {
        JsonObject accInformation = getAccountInformation(account);
        accInformation.put("displayName", account.getDisplayName());
        accInformation.put("shareLocation", account.isSharesLocation());
        accInformation.put("subscription", Repositories.getSubscriptionRepo().getUserSubscription(account));
        accInformation.put("friends", getFriends(account, false));
        accInformation.put("potentialFriends", Repositories.getFriendsRepo().getFriends(account, true).stream().map(UserAccount::getUsername).collect(Collectors.toList()));
        accInformation.put("travelHistory", Repositories.getTravelsRepo().getTravelHistory(account));
        return accInformation;
    }

    public Object getBusinessAccountInformation(BusinessAccount business) {
        JsonObject accInformation = getAccountInformation(business);
        accInformation.put("subscription", Repositories.getSubscriptionRepo().getBusinessSubscription(business));
        accInformation.put("usage", Repositories.getSubscriptionRepo().getBusinessSubscriptionInfo(business));
        return accInformation;
    }

    public int travel(UserAccount acc, int from, int destination, String type) { // getShortEndpoint also validates if endpoint exists
        if (from == destination) throw new EndpointException("Destination and from are the same endpoint");
        return Repositories.getTravelsRepo().travel(acc, new Travel(0, Repositories.getEndpointsRepo().getShortEndpoint(from), Repositories.getEndpointsRepo().getShortEndpoint(destination), PodType.enumOf(type), "", -1));
    }

    public Object getTravelHistory(UserAccount acc) {
        return Repositories.getTravelsRepo().getTravelHistory(acc);
    }

    public void cancelTrip(UserAccount acc, int id) {
        Repositories.getTravelsRepo().cancelTravel(acc, id);
    }

    public Object getCurrentRouteInfo(UserAccount acc) {
        List<Travel> travelList = new LinkedList<>(Repositories.getTravelsRepo().getTravelHistory(acc));
        if (!travelList.isEmpty()) {
            return travelList.get(travelList.size() - 1);
        }
        return "You have not requested any travel pods recently";
    }

    public Object getDeliveries(BusinessAccount acc) {
        return Repositories.getDeliveriesRepo().getDeliveries(acc);
    }

    public Object getDelivery(BaseAccount acc, int id) {
        return Repositories.getDeliveriesRepo().getDeliveryInformation(acc, id);
    }

    public Set<UserAccount> getUsersWhoLiveAt(int id) {
        return userAccounts.stream()
                .filter(acc -> acc.getHomeEndpoint() == id)
                .collect(Collectors.toSet());
    }

    public Set<UserAccount> getFriends(UserAccount account, boolean potentialFriends) {
        return Repositories.getFriendsRepo().getFriends(account, potentialFriends);
    }
}
