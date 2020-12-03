package be.howest.ti.mars.logic.controller.repoTests;

import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.MarsIllegalArgumentException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.AccountsRepository;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FavoritesRepoTest {
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
    void getFavoriteEndpoints(){
        assertEquals(0, Repositories.getFavoritesRepo().getFavoriteEndpoints(testDanny).size());
    }

    @Test
    void favoriteEndpoint(){
        assertEquals(0, Repositories.getFavoritesRepo().getFavoriteEndpoints(testDanny).size());
        Repositories.getFavoritesRepo().favoriteEndpoint(testDanny, 5);
        assertEquals(1, Repositories.getFavoritesRepo().getFavoriteEndpoints(testDanny).size());

        assertThrows(MarsIllegalArgumentException.class, ()->Repositories.getFavoritesRepo().favoriteEndpoint(testDanny, 123));
    }

    @Test
    void unFavoriteEndpoint(){
        assertEquals(1, Repositories.getFavoritesRepo().getFavoriteEndpoints(testDanny).size());
        Repositories.getFavoritesRepo().unFavoriteEndpoint(testDanny, 5);
        assertEquals(0, Repositories.getFavoritesRepo().getFavoriteEndpoints(testDanny).size());

        assertThrows(MarsIllegalArgumentException.class, ()->Repositories.getFavoritesRepo().unFavoriteEndpoint(testDanny, 123));
    }

}
