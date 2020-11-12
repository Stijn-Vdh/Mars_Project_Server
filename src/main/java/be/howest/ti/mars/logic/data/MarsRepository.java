package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.controller.Subscription;
import be.howest.ti.mars.logic.controller.UserAccount;

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

    }

    @Override
    public Set<UserAccount> getUsers() {
        return null;
    }

    @Override
    public void addUser(UserAccount user) {

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
    public void addBusiness(String business) {

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
