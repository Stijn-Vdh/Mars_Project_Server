package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.controller.Delivery;
import be.howest.ti.mars.logic.controller.Endpoint;
import be.howest.ti.mars.logic.controller.Travel;
import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.controller.subscription.BusinessSubscription;
import be.howest.ti.mars.logic.controller.subscription.BusinessSubscriptionInfo;
import be.howest.ti.mars.logic.controller.subscription.UserSubscription;

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
    Set<Travel> getTravelHistory(UserAccount acc);

    void travel(UserAccount user, Travel travel);

    void cancelTravel(UserAccount user, int tripID);

    // Deliveries
    Set<Delivery> getDeliveries(); // TODO: 21-11-2020 also not available in spec

    void addDelivery(Delivery delivery);

    // Subscriptions
    Set<UserSubscription> getUserSubscriptions(); // TODO: 21-11-2020 add to spec and webserver

    Set<BusinessSubscription> getBusinessSubscriptions(); // TODO: 21-11-2020 add to spec and webserver

    UserSubscription getUserSubscription(UserAccount user);

    BusinessSubscription getBusinessSubscription(BusinessAccount business);

    BusinessSubscriptionInfo getBusinessSubscriptionInfo(BusinessAccount business); // this returns the used amount of pods by that business that day

    void setUserSubscription(UserAccount user, int subscriptionId);

    void setBusinessSubscription(BusinessAccount business, int subscriptionId);

    // Report
    Set<String> getReportSections();

    void addReport(BaseAccount baseAccount, String section, String body);
}
