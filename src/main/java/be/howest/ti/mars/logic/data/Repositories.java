package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.data.repoInterfaces.*;
import be.howest.ti.mars.logic.data.repositories.*;

public class Repositories {

    private static final AccountsRepository ACCOUNTS_REPO = new AccountsH2Repository();
    private static final DeliveriesRepository DELIVERIES_REPO = new DeliveriesH2Repository();
    private static final EndpointsRepository ENDPOINTS_REPO = new EndpointsH2Repository();
    private static final FavoritesRepository FAVORITES_REPO = new FavoritesH2Repository();
    private static final FriendsRepository FRIENDS_REPO = new FriendsH2Repository();
    private static final ReportsRepository REPORTS_REPO = new ReportsH2Repository();
    private static final TravelsRepository TRAVELS_REPO = new TravelsH2Repository();
    private static final SubscriptionRepository SUBSCRIPTION_REPO = new SubscriptionH2Repository();

    private Repositories(){}

    public static AccountsRepository getAccountsRepo() {
        return ACCOUNTS_REPO;
    }

    public static DeliveriesRepository getDeliveriesRepo() {
        return DELIVERIES_REPO;
    }

    public static EndpointsRepository getEndpointsRepo() {
        return ENDPOINTS_REPO;
    }

    public static FavoritesRepository getFavoritesRepo() {
        return FAVORITES_REPO;
    }

    public static FriendsRepository getFriendsRepo() {
        return FRIENDS_REPO;
    }

    public static ReportsRepository getReportsRepo() {
        return REPORTS_REPO;
    }

    public static TravelsRepository getTravelsRepo() {
        return TRAVELS_REPO;
    }

    public static SubscriptionRepository getSubscriptionRepo() {
        return SUBSCRIPTION_REPO;
    }
}
