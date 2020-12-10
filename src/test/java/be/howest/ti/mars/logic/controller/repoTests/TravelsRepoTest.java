package be.howest.ti.mars.logic.controller.repoTests;

import be.howest.ti.mars.logic.controller.MTTSController;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.controller.exceptions.EndpointException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.AccountsRepository;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TravelsRepoTest {

    private static final AccountsRepository accountRepo = Repositories.getAccountsRepo();
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
    @Order(2)
    void getTravelHistory() {
        assertEquals(1, Repositories.getTravelsRepo().getTravelHistory(testDanny).size());
    }

    @Test
    @Order(1)
    void travel() {
        assertThrows(EndpointException.class, ()-> controller.travel(testDanny,1,1,"standard"));
        controller.travel(testDanny, 1,2,"standard");
    }

    @Test
    @Order(3)
    void cancelTravel() {
        assertThrows(DatabaseException.class, ()-> controller.cancelTrip(testDanny,2));
        controller.cancelTrip(testDanny, 1);
        assertEquals(0,Repositories.getTravelsRepo().getTravelHistory(testDanny).size());
    }
}