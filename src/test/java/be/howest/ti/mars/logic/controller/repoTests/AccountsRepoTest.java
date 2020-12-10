package be.howest.ti.mars.logic.controller.repoTests;

import be.howest.ti.mars.logic.controller.MTTSController;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.AccountsRepository;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import org.h2.message.DbException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.xml.bind.DataBindingException;
import javax.xml.crypto.Data;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AccountsRepoTest {

    private static final AccountsRepository accountRepo = Repositories.getAccountsRepo();
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
    void addUser() {
        UserAccount accountTestDummy = new UserAccount("Dummy", "Dummy", 8, "Blastreet 23");

        assertEquals(2, accountRepo.getUserAccounts().size());
        accountRepo.addUser(accountTestDummy);
        assertEquals(3, accountRepo.getUserAccounts().size());

        assertThrows(DatabaseException.class,()->accountRepo.addUser(accountTestDummy));
    }

    @Test
    void changePassword() {
        UserAccount accountTestDummy = new UserAccount("Dummy", "Dummy", 8, "Blastreet 23");
        String newPWD = "NotADummy";

        accountRepo.changePassword(accountTestDummy, newPWD);

        accountRepo.getUserAccounts().forEach(userAccount -> {
            if (userAccount.equals(accountTestDummy)) {
                assertEquals(newPWD, userAccount.getPassword());
            }
        });
        assertDoesNotThrow(()->accountRepo.changePassword(accountTestDummy, newPWD));
    }

    @Test
    void setShareLocation() {
        UserAccount accountTestDummy = new UserAccount("Dummy", "Dummy", 8, "Blastreet 23");

        accountRepo.setShareLocation(accountTestDummy, true);
        accountRepo.getUserAccounts().forEach(userAccount -> {
            if (userAccount.equals(accountTestDummy)) {
                assertTrue(userAccount.isSharesLocation());
            }
        });

        assertDoesNotThrow(()->accountRepo.setShareLocation(accountTestDummy, true));
    }

    @Test
    void setDisplayName() {
        UserAccount accountTestDummy = new UserAccount("Dummy", "Dummy", 8, "Blastreet 23");

        accountRepo.setDisplayName(accountTestDummy, "NotADummy");
        accountRepo.getUserAccounts().forEach(userAccount -> {
            if (userAccount.equals(accountTestDummy)) {
                assertEquals(userAccount.getDisplayName(), "NotADummy");
            }
        });

        assertDoesNotThrow(() -> accountRepo.setDisplayName(accountTestDummy, ""));

        accountRepo.getUserAccounts().forEach(userAccount -> {
            if (userAccount.equals(accountTestDummy)) {
                assertEquals(userAccount.getDisplayName(), "Dummy");
            }
        });


    }

    @Test
    void addBusiness() {
        BusinessAccount accountTestDummy = new BusinessAccount("Dummy2", "Dummy", 8, "Blastreet 23");

        assertEquals(1, accountRepo.getBusinessAccounts().size());
        accountRepo.addBusiness(accountTestDummy);
        assertEquals(2, accountRepo.getBusinessAccounts().size());

        assertThrows(DatabaseException.class, ()->accountRepo.addBusiness(accountTestDummy));
    }

    @Test
    void testEquals() {
        UserAccount account2 = new UserAccount("Test");
        UserAccount account1 = new UserAccount("Test");
        assertEquals(account1, account2);
    }
}