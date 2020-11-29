package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import be.howest.ti.mars.logic.data.repoInterfaces.FriendsRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FriendsH2Repository implements FriendsRepository {
    private static final Logger LOGGER = Logger.getLogger(SubscriptionH2Repository.class.getName());

    // Friends SQL QUERIES
    private static final String SQL_SELECT_ALL_FRIENDS = "SELECT * FROM friends f LEFT JOIN users u ON u.name = f.friendName WHERE f.userName=?";
    private static final String SQL_INSERT_FRIEND = "INSERT INTO friends(friendName, userName) VALUES(?,?)";
    private static final String SQL_DELETE_FRIEND = "DELETE FROM friends WHERE friendName=? AND userName=?";


    @Override
    public boolean friendExists(String name, UserAccount user){
        Set<UserAccount> friends = new HashSet<>(Repositories.getFriendsRepo().getFriends(user));
        boolean res = false;
        for (UserAccount friend: friends) {
            if (friend.getUsername().equals(name)){
                res = true;
                break;
            }
        }
        return res;
    }


    @Override
    public Set<UserAccount> getFriends(UserAccount user) {
        Set<UserAccount> friends = new HashSet<>();
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_ALL_FRIENDS)) {
            stmt.setString(1, user.getUsername());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("friendName");
                    friends.add(
                            Repositories.getAccountsRepo().getUserAccounts().stream()
                                    .filter(userAccount -> userAccount.getUsername().equals(name))
                                    .findAny()
                                    .orElseThrow()
                    );
                }
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't view all your friends.");
        }
        return friends;
    }

    @Override
    public void beFriend(String name, String friendName) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_FRIEND)) {

            stmt.setString(1, friendName);
            stmt.setString(2, name);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
            throw new DatabaseException("Can't add a friend.");
        }
    }

    @Override
    public void removeFriend(String name, String friendName) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_DELETE_FRIEND)) {

            stmt.setString(1, friendName);
            stmt.setString(2, name);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage());
            throw new DatabaseException("Can't remove a friend.");
        }
    }
}
