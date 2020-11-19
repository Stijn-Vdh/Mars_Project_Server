package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.controller.*;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.controller.exceptions.EndpointException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

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
    private static final String SQL_GET_ENDPOINT = "SELECT * FROM ENDPOINTS WHERE ID = ?";

    @Override
    public Set<ShortEndpoint> getEndpoints() { //will be short for the meantime
        String SQL_GET_ENDPOINTS = "SELECT * FROM ENDPOINTS";
        Set<ShortEndpoint> endpoints = new HashSet<>();

        try (
                Connection con = MarsConnection.getConnection();
                PreparedStatement stmt = con.prepareStatement(SQL_GET_ENDPOINTS)
        ) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    endpoints.add(new ShortEndpoint(rs.getInt("id"), rs.getString("name")));
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            throw new DatabaseException("Cannot retrieve endpoints");
        }
        return endpoints;
    }

    @Override
    public void addEndpoint(String endpoint) {
        String SQL_INSERT_ENDPOINT = "INSERT INTO ENDPOINTS(name) VALUES(?)";
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_ENDPOINT)) {
            stmt.setString(1, endpoint);
        } catch (SQLException ex) {
            throw new DatabaseException("Can't add endpoint!");
        }
    }

    @Override
    public Endpoint getEndpoint(int id) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_GET_ENDPOINT)
        ) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Endpoint(id, rs.getString("name"), true, "todo", false);
                } else {
                    throw new EndpointException("Endpoint with ID (" + id + ") doesn't exist!");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DatabaseException("Cannot retrieve endpoint with id: " + id + "!");
        }
    }

    private boolean endpointExists(int id) {
        Set<ShortEndpoint> endpoints = new HashSet<>(getEndpoints());
        for (ShortEndpoint endpoint: endpoints) {
            if (endpoint.getId() == id){
                return true;
            }
        }
        return false;
    }

    @Override
    public void favoriteEndpoint_Users(UserAccount user, int id) {

        if (endpointExists(id)){
            String SQL_INSERT_FAVORITE = "insert into favorite_trips_users(userName, endpointID) values(?,?)";

            try (Connection con = MarsConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(SQL_INSERT_FAVORITE)) {
                stmt.setString(1, user.getUsername());
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DatabaseException("Could not favorite endpoint.");
            }
        }else{
            throw new EndpointException("Endpoint does not exist");
        }
    }

    @Override
    public void favoriteEndpoint_Businesses(BusinessAccount business, int id) {
        if (endpointExists(id)){
            String SQL_INSERT_FAVORITE = "insert into favorite_trips_businesses(businessName, endpointID) values(?,?)";


            try (Connection con = MarsConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(SQL_INSERT_FAVORITE)) {
                stmt.setString(1, business.getUsername());
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DatabaseException("Could not unfavorite endpoint.");
            }
        }
    }

    private boolean isFavored(boolean userAcc, int id){
        String SQL_GET_FAVORITES = "select * from ";

        if (userAcc){
            SQL_GET_FAVORITES += "favorite_trips_users";
        }else{
            SQL_GET_FAVORITES += "favorite_trips_businesses";
        }

        try(Connection con = MarsConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(SQL_GET_FAVORITES);
            ResultSet res = stmt.executeQuery()){

            while (res.next()){
                if (res.getInt("endpointID") == id){
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException("Cannot get all favorites.");
        }
    }

    @Override
    public List<JsonObject> getFavoriteTrips(BaseAccount acc, boolean userAcc){
        String SQL_GET_FAVORITES = "select * from ";
        List<JsonObject> favoTrips = new LinkedList<>();
        if (userAcc){
            SQL_GET_FAVORITES += "favorite_trips_users f join endpoints e on f.endpointID = e.id where userName=?";
        }else {
            SQL_GET_FAVORITES += "favorite_trips_businesses f join endpoints e on f.endpointID = e.id where businessName=?";
        }

        try(Connection con = MarsConnection.getConnection();
            PreparedStatement stmt = con.prepareStatement(SQL_GET_FAVORITES)){

            stmt.setString(1, acc.getUsername());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                String endpointName = rs.getString("name");
                int endpointID = rs.getInt("endpointID");

                JsonObject json = new JsonObject();

                json.put("endpointName:", endpointName);
                json.put("id", endpointID);

                favoTrips.add(json);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DatabaseException("Cannot get all favorites.");
        }
        return favoTrips;
    }

    @Override
    public void unFavoriteEndpoint_Users(UserAccount user, int id) {
        if (isFavored(true,id)){
            String SQL_DELETE_FAVORITE = "Delete from favorite_trips_users where userName=? and endpointID=?";

            try (Connection con = MarsConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(SQL_DELETE_FAVORITE)) {
                stmt.setString(1, user.getUsername());
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DatabaseException("Could not unfavorite endpoint.");
            }
        }else{
            throw new EndpointException("This endpoint is not favoured");
        }
    }

    @Override
    public void unFavoriteEndpoint_Businesses(BusinessAccount business, int id) {
        if (isFavored(false,id)){
            String SQL_DELETE_FAVORITE = "delete from favorite_trips_businesses where businessName=? and endpointID=?";

            try (Connection con = MarsConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(SQL_DELETE_FAVORITE)) {
                stmt.setString(1, business.getUsername());
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DatabaseException("Could not favorite endpoint.");
            }
        }else{
            throw new EndpointException("This endpoint is not favoured");
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
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_USER)) {
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
    public void shareLocation(UserAccount user) {
        String SQL_UPDATE_USER = "UPDATE USERS SET sharesLocation=? where name=?";

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_USER)) {

            stmt.setBoolean(1, true);
            stmt.setString(2, user.getUsername());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not share location.");
        }
    }

    @Override
    public void stopSharingLocation(UserAccount user) {
        String SQL_UPDATE_USER = "UPDATE USERS SET sharesLocation=? where name=?";

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_USER)) {

            stmt.setBoolean(1, false);
            stmt.setString(2, user.getUsername());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not share location.");
        }
    }

    @Override
    public List<JsonObject> getFriends(UserAccount user) {
        List<JsonObject> friends = new LinkedList<>();
        String SQL_SELECT_ALL_FRIENDS = "select * from friends f left join users u on u.name = f.friendName where f.userName=?";

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_ALL_FRIENDS)) {
            stmt.setString(1, user.getUsername());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("friendName");
                    int endpointID = rs.getInt("homeEndpointID");
                    String addr = rs.getString("homeAddress");
                    boolean shares = rs.getBoolean("sharesLocation");

                    JsonObject json = new JsonObject();

                    json.put("name:", name);
                    json.put("sharesLocation:", shares);
                    json.put("homeAddress:", addr);
                    json.put("homeEndpointID:", endpointID);

                    friends.add(json);
                }
            }

        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't view all your friends.");
        }
        return friends;
    }

    @Override
    public void beFriend(String name, String friendName) {
        String SQL_INSERT_FRIEND = "Insert into friends(friendName, userName) values(?,?)";
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_FRIEND)) {
            stmt.setString(1, friendName);
            stmt.setString(2, name);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            throw new DatabaseException("Can't add a friend.");
        }
    }

    @Override
    public void removeFriend(String name, String friendName) {
        String SQL_DELETE_FRIEND = "DELETE FROM friends WHERE friendName=? AND userName=?";
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_DELETE_FRIEND)) {
            stmt.setString(1, friendName);
            stmt.setString(2, name);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            throw new DatabaseException("Can't remove a friend.");
        }

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
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_ADD_DELIVERY)) {
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
    public List<Subscription> getSubscriptions() {
        List<Subscription> subscriptions = new LinkedList<>();
        String SQL_SELECT_ALL_SUBSCRIPTIONS = "select * from subscriptions";

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_ALL_SUBSCRIPTIONS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("subscriptionID");
                String name = rs.getString("name");
                int remainingSmallPods_thisDay = rs.getInt("remainingSmallPods_thisDay");
                int remainingLargePods_thisDay = rs.getInt("remainingLargePods_thisDay");
                int dedicatedPods = rs.getInt("amountOfDedicatedPods");


                Subscription sub = new Subscription(id, name, remainingSmallPods_thisDay, remainingLargePods_thisDay, dedicatedPods);
                subscriptions.add(sub);
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't get subscriptions.");
        }
        return subscriptions;
    }

    @Override
    public Subscription getSubscription(BaseAccount acc, boolean userAcc){
        String SQL_SELECT_SUBSCRIPTION_INFO = "select * from ";
        if (userAcc){
            SQL_SELECT_SUBSCRIPTION_INFO += "USERS_subscriptions us join subscriptions s on us.subscriptionID = s.subscriptionID" +
                                            " where userName=?";
        }else{
            SQL_SELECT_SUBSCRIPTION_INFO += "BUSINESSES_SUBSCRIPTIONS bs join subscriptions s on bs.subscriptionID = " +
                                            "s.subscriptionID where businessName=?";
        }

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_SUBSCRIPTION_INFO)
        ) {
            stmt.setString(1, acc.getUsername());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                int subID = rs.getInt("subscriptionID");
                String subName = rs.getString("name");
                if (!userAcc){
                     int remainingSmallPods = rs.getInt("remainingSmallPods_ThisDay");
                     int remainingLargePods = rs.getInt("remainingLargePods_ThisDay");
                     int amountOfDedicatedPods = rs.getInt("amountOfDedicatedPods");
                    return new Subscription(subID, subName, remainingSmallPods, remainingLargePods, amountOfDedicatedPods);
                }
                return new Subscription(subID, subName);
            }else{
                return null;
            }

        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            ex.printStackTrace();
            throw new DatabaseException("Can't get subscription information.");
        }
    }

    @Override
    public void buySubscription(UserAccount user, String subscription) {
        int id = getSubscriptions().stream()
                .filter(sub -> sub.getName().equals(subscription))
                .mapToInt(Subscription::getId).sum();

        String SQL_UPDATE_USER = "UPDATE USERS SET subscriptionID=? where name=?";
        String SQL_INSERT_USER_SUB = "INSERT INTO USERS_SUBSCRIPTIONS(userName,subscriptionID) values(?,?)";

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_USER)) {
            stmt.setInt(1, id);
            stmt.setString(2, user.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't buy a subscription.");
        }

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_USER_SUB)) {
            stmt.setString(1, user.getUsername());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't buy a subscription.");
        }
    }

    @Override
    public void buySubscription(BusinessAccount business, String subscription) {
        int id = getSubscriptions().stream()
                .filter(sub -> sub.getName().equals(subscription))
                .mapToInt(Subscription::getId).sum();

        int remainingSmallPods_thisDay = getSubscriptions().stream()
                .filter(sub -> sub.getName().equals(subscription))
                .mapToInt(Subscription::getRemainingSmallPods_thisDay).sum();

        int remainingLargePods_thisDay = getSubscriptions().stream()
                .filter(sub -> sub.getName().equals(subscription))
                .mapToInt(Subscription::getRemainingLargePods_thisDay).sum();

        int dedicatedPods = getSubscriptions().stream()
                .filter(sub -> sub.getName().equals(subscription))
                .mapToInt(Subscription::getAmountOfDedicatedPods).sum();

        String SQL_UPDATE_BUSINESS = "UPDATE BUSINESSES SET subscriptionID=? where name=?";
        String SQL_INSERT_BUSINESS_SUB = "INSERT INTO BUSINESSES_SUBSCRIPTIONS" +
                "(businessName,subscriptionID, " +
                "remainingSmallPods_thisDay,remainingLargePods_thisDay," +
                "amountOfDedicatedPods) " +
                "values(?,?,?,?,?)";

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_BUSINESS)) {
            stmt.setInt(1, id);
            stmt.setString(2, business.getUsername());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't buy a subscription.");
        }

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_BUSINESS_SUB)) {
            stmt.setString(1, business.getUsername());
            stmt.setInt(2, id);
            stmt.setInt(3, remainingSmallPods_thisDay);
            stmt.setInt(4, remainingLargePods_thisDay);
            stmt.setInt(5, dedicatedPods);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't buy a subscription.");
        }
    }

    @Override
    public void stopSubscription(UserAccount user) {
        int idNoDescriptionActive = 0;
        String SQL_UPDATE_BUSINESS = "UPDATE USERS SET subscriptionID=? where name=?";
        String SQL_DELETE_BUSINESS_SUB = "DELETE FROM USERS_SUBSCRIPTIONS where userName=?";

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_BUSINESS)) {
            stmt.setInt(1, idNoDescriptionActive);
            stmt.setString(2, user.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't stop a subscription.");
        }

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_DELETE_BUSINESS_SUB)) {
            stmt.setString(1, user.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't stop a subscription.");
        }
    }

    @Override
    public void stopSubscription(BusinessAccount business) {
        int idNoDescriptionActive = 0;
        String SQL_UPDATE_BUSINESS = "UPDATE BUSINESSES SET subscriptionID=? where name=?";
        String SQL_DELETE_BUSINESS_SUB = "DELETE FROM BUSINESSES_SUBSCRIPTIONS where businessName=?";

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_BUSINESS)) {
            stmt.setInt(1, idNoDescriptionActive);
            stmt.setString(2, business.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't stop a subscription.");
        }

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_DELETE_BUSINESS_SUB)) {
            stmt.setString(1, business.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't stop a subscription.");
        }
    }


}
