package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.data.repoInterfaces.*;
import be.howest.ti.mars.logic.data.repositories.*;

public class Repositories {

    private static final AccountsRepoInt ACCOUNTS_REPO = new AccountRepository();
    private static final DeliveriesRepoInt DELIVERIES_REPO = new DeliveryRepository();
    private static final EndpointsRepoInt ENDPOINTS_REPO = new EndpointsRepository();
    private static final FavoritesRepoInt FAVORITES_REPO = new FavoritesRepository();
    private static final FriendsRepoInt FRIENDS_REPO = new FriendsRepository();
    private static final ReportsRepoInt REPORTS_REPO = new ReportsRepository();
    private static final TravelsRepoInt TRAVELS_REPO = new TravelsRepository();
    private static final SubscriptionRepoInt SUBSCRIPTION_REPO = new SubscriptionRepository();

    private Repositories(){}

    public static AccountsRepoInt getAccountsRepo() {
        return ACCOUNTS_REPO;
    }

    public static DeliveriesRepoInt getDeliveriesRepo() {
        return DELIVERIES_REPO;
    }

    public static EndpointsRepoInt getEndpointsRepo() {
        return ENDPOINTS_REPO;
    }

    public static FavoritesRepoInt getFavoritesRepo() {
        return FAVORITES_REPO;
    }

    public static FriendsRepoInt getFriendsRepo() {
        return FRIENDS_REPO;
    }

    public static ReportsRepoInt getReportsRepo() {
        return REPORTS_REPO;
    }

    public static TravelsRepoInt getTravelsRepo() {
        return TRAVELS_REPO;
    }

    public static SubscriptionRepoInt getSubscriptionRepo() {
        return SUBSCRIPTION_REPO;
    }
}
