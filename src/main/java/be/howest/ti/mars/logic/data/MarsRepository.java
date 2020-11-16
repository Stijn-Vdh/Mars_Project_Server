package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.controller.BusinessAccount;
import be.howest.ti.mars.logic.controller.Delivery;
import be.howest.ti.mars.logic.controller.Subscription;
import be.howest.ti.mars.logic.controller.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    public List<UserAccount> getFriends(UserAccount user) {
        List<UserAccount> friends = new LinkedList<>();
        String SQL_SELECT_ALL_FRIENDS = "select f.friendName, u.* from friends f join users u on u.name = f.userName where u.name like ?";

        try(Connection con = MarsConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(SQL_SELECT_ALL_FRIENDS))
        {
            stmt.setString(1, '%'+user.getUsername()+'%');

            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){

                    String name = rs.getString("friendName");
                    String pwd = rs.getString("password");
                    int endpointID = rs.getInt("homeEndpointID");
                    String addr = rs.getString("homeAddress");

                    UserAccount friend = new UserAccount(name,pwd,endpointID,addr,null);
                    friends.add(friend);
                }
            }

        }catch (SQLException ex){
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        return friends;
    }

    @Override
    public void beFriend(String name, String friendName) {
        String SQL_INSERT_FRIEND = "Insert into friends(friendName, userName) values(?,?)";
        try(Connection con = MarsConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(SQL_INSERT_FRIEND))
        {
            stmt.setString(1,friendName);
            stmt.setString(2,name);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage());
        }
    }

    @Override
    public void removeFriend(String name, String friendName) {
        String SQL_DELETE_FRIEND = "DELETE FROM friends WHERE friendName=? AND userName=?";
        try(Connection con = MarsConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(SQL_DELETE_FRIEND))
        {
            stmt.setString(1,friendName);
            stmt.setString(2,name);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage());
        }

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
    public Set<Delivery> getDeliveries() {
        return null;
    }

    @Override
    public void addDelivery(Delivery delivery) {
        String SQL_ADD_DELIVERY = "INSERT INTO DELIVERIES(deliveryType, `from`, destination, `date`) VALUES(?,?,?,?)";
        try(Connection con = MarsConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(SQL_ADD_DELIVERY))
     {
         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
         Date parsed = format.parse(delivery.getDate());
         java.sql.Date sqlDate = new java.sql.Date(parsed.getTime());

         stmt.setString(1, delivery.getDeliveryType());
         stmt.setInt(2, delivery.getSourceID());
         stmt.setInt(3, delivery.getDestinationID());
         stmt.setDate(4, sqlDate);
         stmt.executeUpdate();
        } catch (SQLException | ParseException e) {
            System.out.println(e);
           throw new DatabaseException("Can't add delivery!");
        }
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
