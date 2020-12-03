package be.howest.ti.mars.logic.controller.repoTests;

import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.EndpointException;
import be.howest.ti.mars.logic.controller.exceptions.MarsIllegalArgumentException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.AccountsRepository;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class EndpointsRepoTest {

    private static final AccountsRepository repo = Repositories.getAccountsRepo();
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

    @AfterAll
    static void end() {
        MarsConnection.getInstance().cleanUp();
    }

    @Test
    void addEndpoint_1(){
       int totalEndpoints = Repositories.getEndpointsRepo().getEndpoints().size();
       Repositories.getEndpointsRepo().addEndpoint("Brugge");
       assertEquals(totalEndpoints + 1,Repositories.getEndpointsRepo().getEndpoints().size());

       assertThrows(MarsIllegalArgumentException.class, ()-> Repositories.getEndpointsRepo().addEndpoint("Brugge"));
    }

    @Test
    void getEndpoint_2(){
        int totalEndpoints = Repositories.getEndpointsRepo().getEndpoints().size();
        assertThrows(EndpointException.class, ()-> Repositories.getEndpointsRepo().getEndpoint(159753));
        assertEquals("Brugge",Repositories.getEndpointsRepo().getEndpoint(totalEndpoints).getName());
    }

}
