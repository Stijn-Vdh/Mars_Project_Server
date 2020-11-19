package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.controller.*;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.controller.exceptions.EndpointException;
import be.howest.ti.mars.logic.controller.exceptions.EntityNotFoundException;
import io.vertx.core.json.JsonObject;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MarsRepository implements MarsRepoInt {
    private static final Logger LOGGER = Logger.getLogger(MarsRepository.class.getName());
    private static final String SQL_SELECT_FROM = "select * from ";
    //SQL queries
    private static final String SQL_GET_ENDPOINT = "SELECT * FROM ENDPOINTS WHERE ID = ?";
    private static final String SQL_GET_REPORT_SECTIONS = "SELECT * FROM REPORT_SECTIONS";
    private static final String SQL_GET_ENDPOINTS = "SELECT * FROM \"ENDPOINTS\"";
    private static final String SQL_INSERT_REPORTS = "INSERT INTO REPORTS (accountId, reportSection, body) VALUES(?, ?, ?)";
    private static final String SQL_INSERT_ENDPOINT = "INSERT INTO ENDPOINTS(name) VALUES(?)";
    private static final String SQL_UPDATE_USER = "UPDATE USERS SET sharesLocation=? where name=?";
    private static final String SQL_SELECT_ALL_FRIENDS = "select * from friends f left join users u on u.name = f.friendName where f.userName=?";
    private static final String SQL_INSERT_FRIEND = "Insert into friends(friendName, userName) values(?,?)";
    private static final String SQL_DELETE_FRIEND = "DELETE FROM friends WHERE friendName=? AND userName=?";
    private static final String SQL_ADD_DELIVERY = "INSERT INTO DELIVERIES(deliveryType, \"FROM\", destination, \"DATE\") VALUES(?,?,?,?)";
    private static final String SQL_SELECT_ALL_SUBSCRIPTIONS = "select * from subscriptions";
    private static final String SQL_INSERT_TRAVEL = "INSERT INTO TRIPS(\"FROM\",destination,\"DATETIME\",podType) values(?,?,?,?)";
    private static final String SQL_INSERT_TRAVEL_USERS = "INSERT INTO TRIPS_USERS(tripID,userName) values(?,?)";
    private static final String SQL_SELECT_TRAVEL_HISTORY = "SELECT * FROM TRIPS_USERS tu join TRIPS t on tu.tripID = t.tripID where tu.userName=?";

    // Endpoints
    @Override
    public Set<ShortEndpoint> getEndpoints() { //will be short for the meantime
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
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot retrieve endpoints");
        }
        return endpoints;
    }

    @Override
    public void addEndpoint(String endpoint) {

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
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot retrieve endpoint with id: " + id + "!");
        }
    }

    private boolean endpointExists(int id) {
        Set<ShortEndpoint> endpoints = new HashSet<>(getEndpoints());
        for (ShortEndpoint endpoint : endpoints) {
            if (endpoint.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private boolean isFavored(boolean userAcc, int id) {
        String sqlGetFavorites = SQL_SELECT_FROM;

        if (userAcc) {
            sqlGetFavorites += "favorite_trips_users";
        } else {
            sqlGetFavorites += "favorite_trips_businesses";
        }

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlGetFavorites);
             ResultSet res = stmt.executeQuery()) {

            while (res.next()) {
                if (res.getInt("endpointID") == id) {
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot get all favorites.");
        }
    }

    @Override
    public List<JsonObject> getFavoriteTrips(BaseAccount acc, boolean userAcc) {
        String sqlGetFavorites = SQL_SELECT_FROM;
        List<JsonObject> favouredTrips = new LinkedList<>();
        if (userAcc) {
            sqlGetFavorites += "favorite_trips_users f join endpoints e on f.endpointID = e.id where userName=?";
        } else {
            sqlGetFavorites += "favorite_trips_businesses f join endpoints e on f.endpointID = e.id where businessName=?";
        }

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlGetFavorites)) {

            stmt.setString(1, acc.getUsername());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String endpointName = rs.getString("name");
                    int endpointID = rs.getInt("endpointID");

                    JsonObject json = new JsonObject();

                    json.put("endpointName:", endpointName);
                    json.put("id", endpointID);

                    favouredTrips.add(json);
                }
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot get all favorites.");
        }
        return favouredTrips;
    }

    @Override
    public void favoriteEndpoint(BaseAccount acc, int id, boolean userAcc) {
        String sqlInsertFavorite = "insert into ";

        if (userAcc) {
            sqlInsertFavorite += "favorite_trips_users(userName, endpointID) values(?,?)";
        } else {
            sqlInsertFavorite += "favorite_trips_businesses(businessName, endpointID) values(?,?)";
        }

        if (endpointExists(id)) {
            try (Connection con = MarsConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(sqlInsertFavorite)) {
                stmt.setString(1, acc.getUsername());
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                throw new DatabaseException("Could not favorite endpoint.");
            }
        } else {
            throw new EndpointException("Endpoint does not exist");
        }
    }

    @Override
    public void unFavoriteEndpoint(BaseAccount acc, int id, boolean userAcc) {
        String sqlDeleteFavorite;

        if (userAcc) {
            sqlDeleteFavorite = "Delete from favorite_trips_users where userName=? and endpointID=?";

        } else {
            sqlDeleteFavorite = "Delete from favorite_trips_businesses where businessName=? and endpointID=?";
        }

        if (isFavored(userAcc, id)) {
            try (Connection con = MarsConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(sqlDeleteFavorite)) {
                stmt.setString(1, acc.getUsername());
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                throw new DatabaseException("Could not un favorite endpoint.");
            }
        } else {
            throw new EndpointException("This endpoint is not favoured");
        }
    }

    // System
    @Override
    public void addUser(UserAccount user) {
        String sqlInsertUser =
                "INSERT INTO USERS(homeEndpointID, name, password, homeAddress, sharesLocation, subscriptionID) VALUES(?,?,?,?,?,?)";
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlInsertUser)) {
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
        String sqlInsertBusiness = "INSERT INTO BUSINESSES(homeEndpointID, name, password, homeAddress, subscriptionID) VALUES(?,?,?,?,?)";
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlInsertBusiness)) {
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

    // User
    @Override
    public void shareLocation(UserAccount user) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_USER)) {

            stmt.setBoolean(1, true);
            stmt.setString(2, user.getUsername());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not share location.");
        }
    }

    @Override
    public void stopSharingLocation(UserAccount user) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_USER)) {

            stmt.setBoolean(1, false);
            stmt.setString(2, user.getUsername());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not share location.");
        }
    }

    // Friends"
    @Override
    public List<JsonObject> getFriends(UserAccount user) {
        List<JsonObject> friends = new LinkedList<>();


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

    // Travel / Delivery (packages)
    @Override
    public Set<Trip> getTravelHistory(UserAccount acc) {
        Set<Trip> trips = new HashSet<>();
        try (Connection conn = MarsConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_TRAVEL_HISTORY)) {
            stmt.setString(1, acc.getUsername());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()){
                    int from = rs.getInt("from");
                    int destination = rs.getInt("destination");
                    String podType = rs.getString("podType");
                    String date = rs.getString("dateTime");
                    trips.add(new Trip(from, destination, podType, date));
                }

            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not add trip to DB.");
        }
        return trips;
    }

    @Override
    public void travel(UserAccount user, Trip trip) {
        int generatedID;
        try (Connection conn = MarsConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_TRAVEL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, trip.getFrom());
            stmt.setInt(2, trip.getDestination());
            stmt.setString(3, trip.getDateTime());
            stmt.setString(4, trip.getPodType());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                generatedID = rs.getInt(1);
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not add trip to DB");
        }

        try (Connection conn = MarsConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_TRAVEL_USERS)) {
            stmt.setInt(1, generatedID);
            stmt.setString(2, user.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not add trip to DB");
        }

    }

    @Override
    public void cancelTravel(UserAccount user, int tripID) {
        // Empty for now
    }

    @Override
    public Set<Delivery> getDeliveries() {
        return Collections.emptySet();
    }

    @Override
    public void addDelivery(Delivery delivery) {

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
        } catch (SQLException | ParseException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't add delivery!");
        }
    }

    // Subscription
    @Override
    public List<Subscription> getSubscriptions() {
        List<Subscription> subscriptions = new LinkedList<>();


        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_ALL_SUBSCRIPTIONS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("subscriptionID");
                String name = rs.getString("name");
                int remainingSmallPodsThisDay = rs.getInt("remainingSmallPods_thisDay");
                int remainingLargePodsThisDay = rs.getInt("remainingLargePods_thisDay");
                int dedicatedPods = rs.getInt("amountOfDedicatedPods");


                Subscription sub = new Subscription(id, name, remainingSmallPodsThisDay, remainingLargePodsThisDay, dedicatedPods);
                subscriptions.add(sub);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't get subscriptions.");
        }
        return subscriptions;
    }

    @Override
    public Subscription getSubscription(BaseAccount acc, boolean userAcc) {
        String sqlSelectSubscriptionInfo = SQL_SELECT_FROM;
        if (userAcc) {
            sqlSelectSubscriptionInfo += "USERS_subscriptions us join subscriptions s on us.subscriptionID = s.subscriptionID" +
                    " where userName=?";
        } else {
            sqlSelectSubscriptionInfo += "BUSINESSES_SUBSCRIPTIONS bs join subscriptions s on bs.subscriptionID = " +
                    "s.subscriptionID where businessName=?";
        }

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlSelectSubscriptionInfo)
        ) {
            stmt.setString(1, acc.getUsername());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int subID = rs.getInt("subscriptionID");
                    String subName = rs.getString("name");
                    if (!userAcc) {
                        int remainingSmallPods = rs.getInt("remainingSmallPods_ThisDay");
                        int remainingLargePods = rs.getInt("remainingLargePods_ThisDay");
                        int amountOfDedicatedPods = rs.getInt("amountOfDedicatedPods");
                        return new Subscription(subID, subName, remainingSmallPods, remainingLargePods, amountOfDedicatedPods);
                    }
                    return new Subscription(subID, subName);
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't get subscription information.");
        }
    }

    @Override
    public void buySubscription(BaseAccount acc, String subscription, boolean userAcc) {
        int id = getSubscriptions().stream()
                .filter(sub -> sub.getName().equals(subscription))
                .mapToInt(Subscription::getId).sum();

        String sqlUpdateAcc;
        String sqlInsertAccSub = "INSERT INTO ";
        int remainingSmallPodsThisDay = 0;
        int remainingLargePodsThisDay = 0;
        int dedicatedPods = 0;
        // init variables for different type of accounts
        if (userAcc) {
            sqlUpdateAcc = "UPDATE USERS SET subscriptionID=? where name=?";
            sqlInsertAccSub += "USERS_SUBSCRIPTIONS(userName,subscriptionID) values(?,?)";
        } else {
            sqlUpdateAcc = "UPDATE BUSINESSES SET subscriptionID=? where name=?";
            sqlInsertAccSub += "BUSINESSES_SUBSCRIPTIONS(businessName,subscriptionID, remainingSmallPods_thisDay," +
                    "remainingLargePods_thisDay, amountOfDedicatedPods) values(?,?,?,?,?)";

            remainingSmallPodsThisDay = getSubscriptions().stream()
                    .filter(sub -> sub.getName().equals(subscription))
                    .mapToInt(Subscription::getRemainingSmallPodsThisDay)
                    .findAny()
                    .orElse(0);

            remainingLargePodsThisDay = getSubscriptions().stream()
                    .filter(sub -> sub.getName().equals(subscription))
                    .mapToInt(Subscription::getRemainingLargePodsThisDay)
                    .findAny()
                    .orElse(0);

            dedicatedPods = getSubscriptions().stream()
                    .filter(sub -> sub.getName().equals(subscription))
                    .mapToInt(Subscription::getAmountOfDedicatedPods)
                    .findAny()
                    .orElse(0);
        }
        // update acc table
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlUpdateAcc)) {
            stmt.setInt(1, id);
            stmt.setString(2, acc.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't buy a subscription.");
        }
        // update link table (user_subscription | business_subscriptions)
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlInsertAccSub)) {
            stmt.setString(1, acc.getUsername());
            stmt.setInt(2, id);
            if (!userAcc) {
                stmt.setInt(3, remainingSmallPodsThisDay);
                stmt.setInt(4, remainingLargePodsThisDay);
                stmt.setInt(5, dedicatedPods);
            }
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't buy a subscription.");
        }
    }

    @Override
    public void stopSubscription(BaseAccount acc, boolean userAcc) {
        int idNoDescriptionActive = 0;
        String sqlUpdate;
        String sqlDeleteSub;

        if (userAcc) {
            sqlUpdate = "UPDATE USERS SET subscriptionID=? where name=?";
            sqlDeleteSub = "DELETE FROM USERS_SUBSCRIPTIONS where userName=?";
        } else {
            sqlUpdate = "UPDATE BUSINESSES SET subscriptionID=? where name=?";
            sqlDeleteSub = "DELETE FROM BUSINESSES_SUBSCRIPTIONS where businessName=?";
        }

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlUpdate)) {
            stmt.setInt(1, idNoDescriptionActive);
            stmt.setString(2, acc.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't stop a subscription.");
        }

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlDeleteSub)) {
            stmt.setString(1, acc.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't stop a subscription.");
        }
    }

    @Override
    public Set<String> getReportSections() {
        Set<String> sections = new HashSet<>();

        try (
                Connection con = MarsConnection.getConnection();
                PreparedStatement stmt = con.prepareStatement(SQL_GET_REPORT_SECTIONS)
        ) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(rs.getString("name"));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot retrieve report sections");
        }
        return sections;
    }

    @Override
    public void addReport(BaseAccount baseAccount, String section, String body) {
        if (!getReportSections().contains(section))
            throw new EntityNotFoundException("Section (" + section + ") does not currently exist");

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_REPORTS)) {

            stmt.setString(1, baseAccount.getUsername());
            stmt.setString(2, section);
            stmt.setString(3, body);
            stmt.execute();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't add report");
        }
    }
}
