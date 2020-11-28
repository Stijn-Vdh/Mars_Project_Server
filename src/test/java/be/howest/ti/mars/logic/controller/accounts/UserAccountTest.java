package be.howest.ti.mars.logic.controller.accounts;

import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repoInterfaces.AccountsRepoInt;
import be.howest.ti.mars.logic.data.repoInterfaces.FavoritesRepoInt;
import be.howest.ti.mars.logic.data.repoInterfaces.FriendsRepoInt;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import be.howest.ti.mars.logic.data.repositories.SubscriptionRepository;
import be.howest.ti.mars.logic.data.repoInterfaces.SubscriptionRepoInt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserAccountTest {

    private static final AccountsRepoInt repo = Repositories.getAccountsRepoInt();
    private static final UserAccount testDanny = new UserAccount("Danny", "Danny", 5, "MarsStreet 69");
    private static final UserAccount testDebby = new UserAccount("Debby", "Debby", 3, "WestStreet 420");
    private static final UserAccount testPol = new UserAccount("Pol", "Pol", 6, "Earthstreet 23");

    @BeforeAll
    static void start() {

        try {
            MarsConnection.configure("jdbc:h2:~/mars-db", "", "", 9000);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        repo.addUser(testDanny);
        repo.addUser(testDebby);
        repo.addUser(testPol);

    }


    @Test
    void addUsersToDB() {
        UserAccount accountTestDummy = new UserAccount("Dummy", "Dummy", 8, "Blastreet 23");

        assertEquals(3, repo.getUserAccounts().size());
        repo.addUser(accountTestDummy);
        assertEquals(4, repo.getUserAccounts().size());

    }

    @Test
    void testFriendsToDB() {
        FriendsRepoInt friendRepo = Repositories.getFriendsRepoInt();
        Set<UserAccount> users = new HashSet<>();

        users.add(testDanny);
        users.add(testDebby);
        users.add(testPol);

        assertEquals(0, friendRepo.getFriends(testDanny, users).size());
        testDanny.addFriend("Debby");
        testDanny.addFriend("Pol");
        assertEquals(2, friendRepo.getFriends(testDanny, users).size());
        testDanny.removeFriend("Debby");
        assertEquals(1, friendRepo.getFriends(testDanny, users).size());

    }

    @Test
    void testDBEndpoints() {
        FavoritesRepoInt favoRepo = Repositories.getFavoritesRepoInt();
        assertEquals(102, Repositories.getEndpointsRepoInt().getEndpoints().size());
        Repositories.getEndpointsRepoInt().addEndpoint("Home");
        assertEquals(103, Repositories.getEndpointsRepoInt().getEndpoints().size());

        assertEquals(0, favoRepo.getFavoriteEndpoints(testDanny).size());
        System.out.println(repo.getUserAccounts());
        favoRepo.favoriteEndpoint(testDanny, 5);
        favoRepo.favoriteEndpoint(testDanny, 10);
        assertEquals(2, favoRepo.getFavoriteEndpoints(testDanny).size());
        favoRepo.unFavoriteEndpoint(testDanny, 10);
        assertEquals(1, favoRepo.getFavoriteEndpoints(testDanny).size());

    }

    @Test
    void testDBPassword() {
        repo.changePassword(testDanny, "blabla");

        repo.getUserAccounts().forEach(userAccount -> {
            if (userAccount.equals(testDanny)) {
                assertEquals("blabla", userAccount.getPassword());
            }
        });
    }

    @Test
    void testDBSetShareLocation() {
        repo.setShareLocation(testDanny, true);
        repo.getUserAccounts().forEach(userAccount -> {
            if (userAccount.equals(testDanny)) {
                assertTrue(userAccount.isSharesLocation());
            }
        });
    }

    @Test
    void testDBChangeDisplayName() {
        repo.setDisplayName(testDanny, "Den Danny");
        repo.getUserAccounts().forEach(userAccount -> {
            if (userAccount.equals(testDanny)) {
                assertEquals(userAccount.getDisplayName(), "Den Danny");
            }
        });
    }

}