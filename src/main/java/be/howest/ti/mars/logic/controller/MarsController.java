package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.enums.DeliveryType;
import be.howest.ti.mars.logic.controller.enums.PodType;
import be.howest.ti.mars.logic.controller.exceptions.EndpointException;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repoInterfaces.EndpointsRepoInt;
import io.vertx.core.json.JsonObject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MarsController extends AuthController {
    private static final String MOTD = "SmellyEllie";
    private final EndpointsRepoInt endpointRepo = Repositories.getEndpointsRepoInt();

    public String getMessage() {
        return MOTD;
    }

    public int sendPackage(DeliveryType deliveryType, int from, int destination, BaseAccount acc, boolean userAcc) {
        if (!userAcc){
            Repositories.getSubscriptionRepoInt().updateBusinessSubscription(deliveryType.equals(DeliveryType.LARGE),(BusinessAccount) acc);
        }
       return Repositories.getDeliveriesRepoInt().addDelivery(new Delivery(0,deliveryType, Repositories.getEndpointsRepoInt().getShortEndpoint(from), Repositories.getEndpointsRepoInt().getShortEndpoint(destination), "", acc.getUsername()));
    }

    public Object addFriend(UserAccount user, String friendName) { // TODO: 20-11-2020 validation friend exists and not already friended and user and friend not same
        if (userAccounts.contains(new UserAccount(friendName))) {
            user.addFriend(friendName);
        } else {
            throw new UsernameException("Could not add a friend with the given username");
        }
        return "You just added a friend called:" + friendName;
    }

    public Object removeFriend(UserAccount user, String friendName) { // TODO: 20-11-2020 validation friend exists and not friended  and user and friend not same
        if (userAccounts.contains(new UserAccount(friendName))) {
            user.removeFriend(friendName);
        } else {
            throw new UsernameException("Could not remove a friend with the given username");
        }
        return "You just removed a friend called:" + friendName;
    }

    public void favoriteEndpoint(BaseAccount acc, int id) {
        Repositories.getFavoritesRepoInt().favoriteEndpoint(acc, id);
    }

    public void unFavoriteEndpoint(BaseAccount acc, int id) {
        Repositories.getFavoritesRepoInt().unFavoriteEndpoint(acc, id);
    }

    private JsonObject getAccountInformation(BaseAccount acc) {
        JsonObject accInformation = new JsonObject();
        accInformation.put("name:", acc.getUsername());
        accInformation.put("homeAddress:", acc.getAddress());
        accInformation.put("homeEndpoint:", acc.getHomeAddressEndpoint());
        accInformation.put("favouriteEndpoints:", Repositories.getFavoritesRepoInt().getFavoriteEndpoints(acc));
        return accInformation;
    }

    public Object getUserAccountInformation(UserAccount account) {
        JsonObject accInformation = getAccountInformation(account);
        accInformation.put("displayName:", account.getDisplayName());
        accInformation.put("shareLocation:", account.isSharesLocation());
        accInformation.put("subscription:", Repositories.getSubscriptionRepoInt().getUserSubscription(account));
        accInformation.put("friends:", Repositories.getFriendsRepoInt().getFriends(account, userAccounts).stream().map(UserAccount::getUsername).collect(Collectors.toList()));
        accInformation.put("travelHistory:", Repositories.getTravelsRepoInt().getTravelHistory(account));
        return accInformation;
    }

    public Object getBusinessAccountInformation(BusinessAccount business) {
        JsonObject accInformation = getAccountInformation(business);
        accInformation.put("subscription:", Repositories.getSubscriptionRepoInt().getBusinessSubscription(business));
        accInformation.put("Current usage subscription:", Repositories.getSubscriptionRepoInt().getBusinessSubscriptionInfo(business));
        return accInformation;
    }

    public int travel(UserAccount acc, int from, int destination, String type) { // getShortEndpoint also validates if endpoint exists
        if (from == destination) throw new EndpointException("Destination and from are the same endpoint");
        return Repositories.getTravelsRepoInt().travel(acc, new Travel(0,Repositories.getEndpointsRepoInt().getShortEndpoint(from), Repositories.getEndpointsRepoInt().getShortEndpoint(destination), PodType.enumOf(type), ""));
    }

    public Object getTravelHistory(UserAccount acc) {
        return Repositories.getTravelsRepoInt().getTravelHistory(acc);
    }

    public void cancelTrip(UserAccount acc, int id) {
        Repositories.getTravelsRepoInt().cancelTravel(acc, id);
    }

    public Object getCurrentRouteInfo(UserAccount acc) {
        List<Travel> travelList = new LinkedList<>(Repositories.getTravelsRepoInt().getTravelHistory(acc));
        if (!travelList.isEmpty()){
            return travelList.get(travelList.size()-1);
        }
        return "You have not requested any travel pods recently";
    }

    public Object getDeliveries(BusinessAccount acc) {
        return Repositories.getDeliveriesRepoInt().getDeliveries(acc);
    }

    public Object getDelivery(BaseAccount acc, int id) {
        return Repositories.getDeliveriesRepoInt().getDeliveryInformation(acc, id);
    }
}
