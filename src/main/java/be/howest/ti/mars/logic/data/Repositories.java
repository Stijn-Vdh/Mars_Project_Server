package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.data.repoInterfaces.*;
import be.howest.ti.mars.logic.data.repositories.*;

public class Repositories {

    private static final AccountsRepoInt ACCOUNTS_REPO_INT = new AccountRepository();
    private static final DeliveriesRepoInt DELIVERIES_REPO_INT = new DeliveryRepository();
    private static final EndpointsRepoInt ENDPOINTS_REPO_INT= new EndpointsRepository();
    private static final FavoritesRepoInt FAVORITES_REPO_INT = new FavoritesRepository();
    private static final FriendsRepoInt FRIENDS_REPO_INT = new FriendsRepository();
    private static final ReportsRepoInt REPORTS_REPO_INT = new ReportsRepository();
    private static final TravelsRepoInt TRAVELS_REPO_INT = new TravelsRepository();

    private Repositories(){}

    public static AccountsRepoInt getAccountsRepoInt() {
        return ACCOUNTS_REPO_INT;
    }

    public static DeliveriesRepoInt getDeliveriesRepoInt() {
        return DELIVERIES_REPO_INT;
    }

    public static EndpointsRepoInt getEndpointsRepoInt() {
        return ENDPOINTS_REPO_INT;
    }

    public static FavoritesRepoInt getFavoritesRepoInt() {
        return FAVORITES_REPO_INT;
    }

    public static FriendsRepoInt getFriendsRepoInt() {
        return FRIENDS_REPO_INT;
    }

    public static ReportsRepoInt getReportsRepoInt() {
        return REPORTS_REPO_INT;
    }

    public static TravelsRepoInt getTravelsRepoInt() {
        return TRAVELS_REPO_INT;
    }
}
