package be.howest.ti.mars.logic.controller.accounts;

import be.howest.ti.mars.logic.controller.MarsController;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.data.MarsConnection;
import be.howest.ti.mars.logic.data.MarsH2Repository;
import be.howest.ti.mars.logic.data.MarsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDBAccountTest {

    private static final MarsRepository repo = new MarsH2Repository();
    private static UserAccount testDanny = new UserAccount("Danny", "Danny", 5, "MarsStreet 69");
    private static UserAccount testDebby = new UserAccount("Debby", "Debby", 3, "WestStreet 420");
    private static UserAccount testPol = new UserAccount("Pol", "Pol", 6, "Earthstreet 23");
    private static final String SQL_GET_USERS = "SELECT * from users \n" +
            "JOIN accounts ON users.name = accounts.name";



    public Set<UserAccount> getUsers() {
        Set<UserAccount> users = new HashSet<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_GET_USERS)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new UserAccount(rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("displayname"),
                            rs.getInt("homeendpointid"),
                            rs.getString("homeaddress"),
                            rs.getBoolean("shareslocation")));
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Cannot retrieve users");
        }
        return users;
    }

    @BeforeAll
    static void start(){

        try {
            MarsConnection.configure("jdbc:h2:~/mars-db","","",9000);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        repo.addUser(testDanny);
        repo.addUser(testDebby);
        repo.addUser(testPol);

    }


    @Test
    void addUsersToDB()
    {
        UserAccount accountTestDummy = new UserAccount("Dummy", "Dummy", 8, "Blastreet 23");

        assertEquals(3, getUsers().size());
        repo.addUser(accountTestDummy);
        assertEquals(4, getUsers().size());

    }

    @Test
    void testFriendsToDB(){
        Set<UserAccount> users = new HashSet<>();

        users.add(testDanny);
        users.add(testDebby);
        users.add(testPol);

        assertEquals(0, repo.getFriends(testDanny, users).size());
        testDanny.addFriend("Debby");
        testDanny.addFriend("Pol");
        assertEquals(2, repo.getFriends(testDanny, users).size());
        testDanny.removeFriend("Debby");
        assertEquals(1, repo.getFriends(testDanny, users).size());

    }

    @Test
    void testDBEndpoints() {
        assertEquals(102, repo.getEndpoints().size());
        repo.addEndpoint("Home");
        assertEquals(103,repo.getEndpoints().size());

        assertEquals(0, repo.getFavoriteEndpoints(testDanny).size());
        System.out.println(getUsers());
        repo.favoriteEndpoint(testDanny, 5);
        repo.favoriteEndpoint(testDanny, 10);
        assertEquals(2, repo.getFavoriteEndpoints(testDanny).size());
        repo.unFavoriteEndpoint(testDanny, 10);
        assertEquals(1, repo.getFavoriteEndpoints(testDanny).size());

    }

//    @Test
//    void testDBPassword(){
//        repo.changePassword(testDanny, "blabla");
//        getUsers().forEach(userAccount -> {
//            if (userAccount.getUsername().equals(testDanny.getUsername())){
//                assertEquals(userAccount.getPassword(), "blabla");
//            }
//        });
//
//    }

    @Test
    void testDBSetShareLocation(){
        repo.setShareLocation(testDanny, true);
        getUsers().forEach(userAccount -> {
            if (userAccount.equals(testDanny)){
                assertTrue(userAccount.isSharesLocation());
            }
        });
    }

    @Test
    void testDBChangeDisplayName(){
        repo.setDisplayName(testDanny, "Den Danny");
        getUsers().forEach(userAccount -> {
            if (userAccount.equals(testDanny)){
                assertEquals(userAccount.getDisplayName(), "Den Danny");
            }
        });
    }

}