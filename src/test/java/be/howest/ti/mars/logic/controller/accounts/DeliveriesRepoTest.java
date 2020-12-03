package be.howest.ti.mars.logic.controller.accounts;

import be.howest.ti.mars.logic.controller.Delivery;
import be.howest.ti.mars.logic.controller.MTTSController;
import be.howest.ti.mars.logic.controller.enums.DeliveryType;
import be.howest.ti.mars.logic.controller.exceptions.AuthenticationException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.AccountsRepository;
import be.howest.ti.mars.logic.data.repositories.DeliveriesRepository;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeliveriesRepoTest {

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

    @Test
    void addDelivery(){
        assertEquals(1,controller.sendPackage(DeliveryType.SMALL,1,2,testDanny, true));
        assertEquals(2,controller.sendPackage(DeliveryType.SMALL,1,2,testDanny, true));

        assertEquals(3,controller.sendPackage(DeliveryType.SMALL,1,2,testDebby, true));

        assertThrows(AuthenticationException.class, () -> controller.sendPackage(DeliveryType.LARGE, 1,2,testDebby, true));
        assertThrows(AuthenticationException.class, () -> controller.sendPackage(DeliveryType.SMALL, 1,1,testDebby, true));

        assertEquals(4,controller.sendPackage(DeliveryType.SMALL,1,2,testPol, false));
    }

    @Test
    void getDeliveryInformation(){
        Delivery delivery1 = (Delivery) controller.getDelivery(testDanny,1);
        assertEquals(DeliveryType.SMALL, delivery1.getDeliveryType());

        Delivery delivery2 = (Delivery) controller.getDelivery(testDebby,3);
        assertEquals(Repositories.getEndpointsRepo().getShortEndpoint(2), delivery2.getDestination());
        assertEquals(Repositories.getEndpointsRepo().getShortEndpoint(1), delivery2.getSource());
        assertEquals("Debby", delivery2.getSender());
    }

}
