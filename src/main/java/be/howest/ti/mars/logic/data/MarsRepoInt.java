package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.controller.BaseAccount;
import be.howest.ti.mars.logic.controller.BusinessAccount;
import be.howest.ti.mars.logic.controller.Delivery;
import be.howest.ti.mars.logic.controller.Subscription;
import be.howest.ti.mars.logic.controller.UserAccount;

import java.util.List;
import java.util.Set;

public interface MarsRepoInt {
    Set<String> getEndpoints(); // todo: String moet endpoint class worden
    void addEndpoint(String endpoint);

    // User
    void addUser(UserAccount user);
    void ShareLocation(UserAccount user, Boolean shareLocation);

    // Friends
    List<UserAccount> getFriends(UserAccount user);
    void beFriend(String name, String friendName);
    void removeFriend(String name, String friendName);
    void getFriendLocation(int friendID);

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
    void buySubscription (UserAccount user, String subscription);
    void buySubscription (BusinessAccount business, String subscription);
    void stopSubscription (UserAccount user);
    void stopSubscription (BusinessAccount business);



}
