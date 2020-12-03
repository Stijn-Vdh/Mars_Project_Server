package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.subscription.BusinessSubscription;
import be.howest.ti.mars.logic.controller.subscription.BusinessSubscriptionInfo;
import be.howest.ti.mars.logic.controller.subscription.UserSubscription;

import java.util.Set;

public interface SubscriptionRepository {
    Set<UserSubscription> getUserSubscriptions(); // TODO: 21-11-2020 add to spec and webserver

    Set<BusinessSubscription> getBusinessSubscriptions(); // TODO: 21-11-2020 add to spec and webserver

    UserSubscription getUserSubscription(UserAccount user);

    BusinessSubscription getBusinessSubscription(BusinessAccount business);

    BusinessSubscriptionInfo getBusinessSubscriptionInfo(BusinessAccount business); // this returns the used amount of pods by that business that day

    void updateBusinessSubscription(boolean b, BusinessAccount acc);

    void setUserSubscription(UserAccount user, int subscriptionId);

    void setBusinessSubscription(BusinessAccount business, int subscriptionId);

    void resetPods(BusinessAccount acc);


}
