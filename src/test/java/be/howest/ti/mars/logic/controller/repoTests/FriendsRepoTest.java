package be.howest.ti.mars.logic.controller.repoTests;

import be.howest.ti.mars.logic.controller.MTTSController;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.controller.exceptions.EntityNotFoundException;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.AccountsRepository;
import be.howest.ti.mars.logic.data.repositories.FriendsRepository;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(1)
    void friendExists() {
        assertFalse(friendRepo.friendExists("Debby", testDanny));
        friendRepo.beFriend(testDanny.getUsername(),"Debby", false);
        assertTrue(friendRepo.friendExists("Debby", testDanny));
    }

    @Test
    @Order(2)
    void beFriend() {
        assertEquals(0, friendRepo.getFriends(testDebby, false).size());
        controller.addFriend(testDebby,"Danny");
        assertEquals(1, friendRepo.getFriends(testDanny, true).size());
        assertEquals(1, friendRepo.getFriends(testDebby,false).size());

        assertThrows(UsernameException.class, () -> controller.addFriend(testDanny, "Debby"));
    }

    @Test
    @Order(3)
    void removeFriend() {
        assertEquals(1, friendRepo.getFriends(testDebby, false).size());
        friendRepo.removeFriend(testDebby.getUsername(),"Danny", false);
        assertEquals(0, friendRepo.getFriends(testDebby, false).size());

    }
}