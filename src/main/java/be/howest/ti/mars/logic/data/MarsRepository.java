package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.controller.BusinessAccount;
import be.howest.ti.mars.logic.controller.Subscription;
import be.howest.ti.mars.logic.controller.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import org.h2.engine.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MarsRepository implements MarsRepoInt {
    private static final Logger logger = Logger.getLogger(MarsRepository.class.getName());
    @Override
    public Set<String> getEndpoints() {
        return null;
    }

    @Override
    public void addEndpoint(String endpoint) {
        String SQL_INSERT_ENDPOINT = "INSERT INTO ENDPOINTS(name) VALUES(?)";
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_ENDPOINT)){
            stmt.setString(1, endpoint);
        } catch (SQLException ex) {
            throw new DatabaseException("Can't add endpoint!");
        }
    }

    @Override
    public Set<UserAccount> getUsers() {
        return null;
    }

    @Override
    public void addUser(UserAccount user) {
        String SQL_INSERT_USER =
                "INSERT INTO USERS(homeEndpointID, name, password, homeAddress, sharesLocation, subscriptionID) VALUES(?,?,?,?,?,?)";
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_USER)) {
            stmt.setInt(1, user.getHomeAddressEndpoint());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getAddress());
            stmt.setBoolean(5, false);
            stmt.setInt(6, 0);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("Cannot add user!");
        }
    }

    @Override
    public void addBusiness(BusinessAccount business) {
        String SQL_INSERT_USER = "INSERT INTO BUSINESSES(homeEndpointID, name, password, homeAddress, subscriptionID) VALUES(?,?,?,?,?)";
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_USER)){
            stmt.setInt(1, business.getHomeAddressEndpoint());
            stmt.setString(2, business.getUsername());
            stmt.setString(3, business.getPassword());
            stmt.setString(4, business.getAddress());
            stmt.setInt(5, 0);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("Cannot add business!");
        }

    }

        @Override
    public void ShareLocation(UserAccount user, Boolean shareLocation) {

    }

    @Override
    public Set<UserAccount> getFriends(UserAccount user) {
        return null;
    }

    @Override
    public void addFriend(int userID, int friendID) {

    }

    @Override
    public void removeFriend(int userID, int friendID) {

    }

    @Override
    public void getFriendLocation(int friendID) {

    }

    @Override
    public Set<String> getBusinesses() {
        return null;
    }


    @Override
    public Set<String> getTrips() {
        return null;
    }

    @Override
    public void addTrip(String trip) {

    }

    @Override
    public void travel(UserAccount user, String trip) {

    }

    @Override
    public void cancelTravel(UserAccount user, String trip) {

    }

    @Override
    public Set<String> getDeliveries() {
        return null;
    }

    @Override
    public void sendSmallPackage(UserAccount user, String delivery) {

    }

    @Override
    public void sendLargePackage(UserAccount user, String delivery) {

    }

    @Override
    public void addDelivery(String delivery) {

    }

    @Override
    public Set<Subscription> getSubscriptions() {
        Set<Subscription> subscriptions = new HashSet<>();
        String SQL_SELECT_ALL_SUBSCRIPTIONS = "select * from subscriptions";

        try(Connection con = MarsConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(SQL_SELECT_ALL_SUBSCRIPTIONS);
            ResultSet rs = stmt.executeQuery()){

            while(rs.next()){
                int id = rs.getInt("subscriptionID");
                String name = rs.getString("name");

                Subscription sub = new Subscription(id, name);
                subscriptions.add(sub);
            }
        }catch (SQLException ex){
               logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        return subscriptions;
    }

    @Override
    public void getSubscriptionInfo(int businessID) {

    }

    @Override
    public void buySubscription(UserAccount user, String subscription) {

    }

    @Override
    public void buySubscription(String business, String subscription) {

    }

    @Override
    public void removeSubscription(UserAccount user, String subscription) {

    }

    @Override
    public void removeSubscription(String business, String subscription) {

    }
}
