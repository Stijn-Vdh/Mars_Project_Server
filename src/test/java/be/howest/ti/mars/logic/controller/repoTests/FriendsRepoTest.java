package be.howest.ti.mars.logic.controller.repoTests;

import be.howest.ti.mars.logic.controller.MTTSController;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.UsernameException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.AccountsRepository;
import be.howest.ti.mars.logic.data.repositories.FriendsRepository;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        controller.createAccount(testDanny.getUsername(), testDanny.getPassword(), testDanny.getAddress(), testDanny.getHomeAddressEndpoint(), false);
        controller.createAccount(testDebby.getUsername(), testDebby.getPassword(), testDebby.getAddress(), testDebby.getHomeAddressEndpoint(), false);
        controller.createAccount(testPol.getUsername(), testPol.getPassword(), testPol.getAddress(), testPol.getHomeAddressEndpoint(), false);
    }

    @AfterAll
    static void end() {
        MarsConnection.getInstance().cleanUp();
    }


    @Test
    @Order(1)
    void beFriend() {
        assertEquals(0, friendRepo.getFriends(testDebby, false).size());
        controller.addFriend(testDebby, "Danny");
        assertEquals(1, friendRepo.getFriends(testDanny, true).size());
        assertEquals(1, friendRepo.getFriends(testDebby, false).size());

        controller.addFriend(testDanny, "Debby");
        assertEquals(0, friendRepo.getFriends(testDanny, true).size());
        assertEquals(1, friendRepo.getFriends(testDanny, false).size());
        assertEquals(0, friendRepo.getFriends(testDebby, true).size());

        assertThrows(UsernameException.class, () -> controller.addFriend(testDanny, "Debby"));
    }

    @Test
    @Order(2)
    void removeFriend() {
        assertEquals(1, friendRepo.getFriends(testDebby, false).size());
        assertEquals(1, friendRepo.getFriends(testDanny, false).size());
        controller.removeFriend(testDebby, "Danny");
        assertEquals(0, friendRepo.getFriends(testDebby, false).size());
        assertEquals(0, friendRepo.getFriends(testDanny, false).size());

        controller.addFriend(testDanny, "Debby");
        assertEquals(1, friendRepo.getFriends(testDanny, false).size());
        assertEquals(1, friendRepo.getFriends(testDebby, true).size());
        controller.removeFriend(testDebby, "Danny");

        assertThrows(UsernameException.class, () -> controller.removeFriend(testDanny, "Debby"));

    }
}