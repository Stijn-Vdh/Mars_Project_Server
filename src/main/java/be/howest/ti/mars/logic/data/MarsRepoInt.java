package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.controller.*;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import io.vertx.core.json.JsonObject;


import java.util.List;
import java.util.Set;

public interface MarsRepoInt {
    // Endpoint
    Set<ShortEndpoint> getEndpoints();

    void addEndpoint(String endpoint);

    Endpoint getEndpoint(int id);

    // Favorite
    List<JsonObject> getFavoriteTrips(BaseAccount acc, boolean userAcc);

    void favoriteEndpoint(BaseAccount acc, int id, boolean userAcc);

    void unFavoriteEndpoint(BaseAccount user, int id, boolean userAcc);

    // User
    void addUser(UserAccount user);

    void shareLocation(UserAccount user);

    void stopSharingLocation(UserAccount userAccount);

    // Friends
    List<JsonObject> getFriends(UserAccount user);

    void beFriend(String name, String friendName);

    void removeFriend(String name, String friendName);

    void addBusiness(BusinessAccount business);

    // Travel
    Set<Trip> getTravelHistory(UserAccount acc);

    void travel(UserAccount user, Trip trip);

    void cancelTravel(UserAccount user, int tripID);

    // Deliveries
    Set<Delivery> getDeliveries();

    void addDelivery(Delivery delivery);

    // Subscriptions
    List<Subscription> getSubscriptions();

    Subscription getSubscription(BaseAccount acc, boolean userAcc);

    void buySubscription(BaseAccount acc, String subscription, boolean userAcc);

    void stopSubscription(BaseAccount acc, boolean userAcc);


    // Report
    Set<String> getReportSections();

    void addReport(BaseAccount baseAccount, String section, String body);
}
