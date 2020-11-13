package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.controller.Subscription;
import be.howest.ti.mars.logic.controller.UserAccount;

import java.util.Set;

public interface MarsRepoInt {
    Set<String> getEndpoints(); // todo: String moet endpoint class worden
    void addEndpoint(String endpoint);

    // User
    Set<UserAccount> getUsers();
    void addUser(UserAccount user);
    void ShareLocation(UserAccount user, Boolean shareLocation);

    // Friends
    Set<UserAccount> getFriends(UserAccount user);
    void beFriend(String name, String friendName);
    void removeFriend(String name, String friendName);
    void getFriendLocation(int friendID);

    // Business
    Set<String> getBusinesses(); // todo:  String moet business class worden
    void addBusiness(String business);

    // Travel
    Set<String> getTrips(); // todo:  String moet trip class worden
    void addTrip(String trip);
    void travel(UserAccount user, String trip);
    void cancelTravel(UserAccount user, String trip);

    // Deliveries
    Set<String> getDeliveries(); // todo:  String moet delivery class worden
    void sendSmallPackage(UserAccount user, String delivery);
    void sendLargePackage(UserAccount user, String delivery);
    void addDelivery(String delivery);

    // Subscriptions
    Set<Subscription> getSubscriptions();
    void getSubscriptionInfo(int businessID);
    void buySubscription (UserAccount user, String subscription);
    void buySubscription (String business, String subscription);
    void removeSubscription (UserAccount user, String subscription);
    void removeSubscription (String business, String subscription);



}
