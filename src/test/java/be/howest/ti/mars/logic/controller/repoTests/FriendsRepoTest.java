package be.howest.ti.mars.logic.controller.repoTests;

import be.howest.ti.mars.logic.controller.MTTSController;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.AccountsRepository;
import be.howest.ti.mars.logic.data.repositories.FriendsRepository;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class FriendsRepoTest {
    private static final AccountsRepository accountRepo = Repositories.getAccountsRepo();
    private static final FriendsRepository friendRepo = Repositories.getFriendsRepo();
    private static final MTTSController controller = new MTTSController();
    private static final UserAccount testDanny = new UserAccount("Danny", "Danny", 5, "MarsStreet 69");
    private static final UserAccount testDebby = new UserAccount("Debby", "Debby", 3, "WestStreet 420");
    private static final BusinessAccount testPol = new BusinessAccount("Pol", "Pol", 6, "Earthstreet 23");

    @BeforeAll
    static void start() {

        try {
            MarsConnection.configure("jdbc:h2:~/mars-db", "", "", 9000);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        accountRepo.addUser(testDanny);
        accountRepo.addUser(testDebby);
        accountRepo.addBusiness(testPol);
    }

    @AfterAll
    static void end() {
        MarsConnection.getInstance().cleanUp();
    }


    @Test
    void friendExists() {
        assertFalse(friendRepo.friendExists("Debby", testDanny));
        controller.addFriend(testDanny, "Debby");
        assertTrue(friendRepo.friendExists("Debby", testDanny));
    }

    @Test
    void beFriend() {
        assertEquals(1, friendRepo.getFriends(testDanny).size());
        controller.addFriend(testDebby,"Danny");
        assertThrows(DatabaseException.class, () -> controller.addFriend(testDanny, "Debby"));
    }

    @Test
    void removeFriend() {

    }
}