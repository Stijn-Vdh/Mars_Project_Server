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
    void favoriteEndpoint_Users(UserAccount user, int id);
    void favoriteEndpoint_Businesses(BusinessAccount business, int id);

    void unFavoriteEndpoint_Users(UserAccount user, int id);

    void unFavoriteEndpoint_Businesses(BusinessAccount business, int id);

    // User
    void addUser(UserAccount user);

    void shareLocation(UserAccount user);

    void stopSharingLocation(UserAccount userAccount);

    // Friends
    List<JsonObject> getFriends(UserAccount user);

    void beFriend(String name, String friendName);

    void removeFriend(String name, String friendName);

    // Business
    Set<String> getBusinesses(); // todo:  String moet business class worden

    void addBusiness(BusinessAccount business);

    // Travel
    Set<String> getTrips(); // todo:  String moet trip class worden

    void addTrip(String trip);

    void travel(UserAccount user, String trip);

    void cancelTravel(UserAccount user, String trip);

    // Deliveries
    Set<Delivery> getDeliveries();

    void addDelivery(Delivery delivery);

    // Subscriptions
    List<Subscription> getSubscriptions();

    Subscription getSubscriptionInfo(String businessName);

    void buySubscription(UserAccount user, String subscription);

    void buySubscription(BusinessAccount business, String subscription);

    void stopSubscription(UserAccount user);

    void stopSubscription(BusinessAccount business);

    // Report
    Set<String> getReportSections();

    void addReport(BaseAccount baseAccount, String section, String body);
}
