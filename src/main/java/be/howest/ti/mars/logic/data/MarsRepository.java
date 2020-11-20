package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.controller.*;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;

import java.util.List;
import java.util.Set;

public interface MarsRepository {
    // account
    void addAccount(BaseAccount account);

    // Endpoint
    Set<ShortEndpoint> getEndpoints();

    void addEndpoint(String endpoint);

    Endpoint getEndpoint(int id);

    // Favorite
    Set<ShortEndpoint> getFavoriteEndpoints(BaseAccount acc);

    void favoriteEndpoint(BaseAccount acc, int id);

    void unFavoriteEndpoint(BaseAccount user, int id);

    // User
    void addUser(UserAccount user);

    void setShareLocation(UserAccount user, boolean shareLocation);


    // Friends
    Set<UserAccount> getFriends(UserAccount user, Set<UserAccount> users);

    void beFriend(String name, String friendName);

    void removeFriend(String name, String friendName);

    // business
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
