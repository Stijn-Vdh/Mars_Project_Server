package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.controller.Delivery;
import be.howest.ti.mars.logic.controller.Endpoint;
import be.howest.ti.mars.logic.controller.Travel;
import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.controller.enums.PodType;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.controller.exceptions.EndpointException;
import be.howest.ti.mars.logic.controller.exceptions.EntityNotFoundException;
import be.howest.ti.mars.logic.controller.subscription.BusinessSubscription;
import be.howest.ti.mars.logic.controller.subscription.BusinessSubscriptionInfo;
import be.howest.ti.mars.logic.controller.subscription.UserSubscription;

import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MarsH2Repository implements MarsRepository {
    private static final Logger LOGGER = Logger.getLogger(MarsH2Repository.class.getName());
    // SQL queries
    // Endpoints
    private static final String SQL_GET_ENDPOINT = "SELECT * FROM ENDPOINTS WHERE ID = ?";
    private static final String SQL_GET_ENDPOINTS = "SELECT * FROM ENDPOINTS";
    private static final String SQL_INSERT_ENDPOINT = "INSERT INTO ENDPOINTS(name) VALUES(?)";
    // Reports
    private static final String SQL_GET_REPORT_SECTIONS = "SELECT * FROM REPORT_SECTIONS";
    private static final String SQL_INSERT_REPORTS = "INSERT INTO REPORTS VALUES(DEFAULT, ?, ?, ?)";
    // Friends
    private static final String SQL_SELECT_ALL_FRIENDS = "SELECT * FROM friends f LEFT JOIN users u ON u.name = f.friendName WHERE f.userName=?";
    private static final String SQL_INSERT_FRIEND = "INSERT INTO friends(friendName, userName) VALUES(?,?)";
    private static final String SQL_DELETE_FRIEND = "DELETE FROM friends WHERE friendName=? AND userName=?";
    // Deliveries
    private static final String SQL_ADD_DELIVERY = "INSERT INTO DELIVERIES VALUES(DEFAULT, ?, ?, ?, DEFAULT, ?)";
    // Travels
    private static final String SQL_INSERT_TRAVEL = "INSERT INTO TRAVELS VALUES(default, ?, ?, ?, DEFAULT, ?, NULL)";
    private static final String SQL_SELECT_TRAVEL_HISTORY = "SELECT * FROM TRAVELS t WHERE userName=? ";
    private static final String SQL_DELETE_TRAVEL = "DELETE FROM TRAVELS WHERE userName=? AND ID=?";
    // Favorites
    private static final String SQL_DELETE_FAVORITE_ENDPOINT = "DELETE FROM favorite_endpoints WHERE ACCOUNTNAME=? AND ENDPOINTID=?;";
    private static final String SQL_INSERT_FAVORITE_ENDPOINT = "INSERT INTO favorite_endpoints VALUES (?, ?)";
    private static final String SQL_SELECT_FAVORITE_ENDPOINT = "SELECT * FROM favorite_endpoints fe JOIN endpoints e ON fe.endpointid = e.id WHERE accountname = ?";
    // Accounts
    private static final String SQL_INSERT_ACCOUNT = "INSERT INTO accounts VALUES (?, ?, ?, ?)";
    private static final String SQL_INSERT_USER = "INSERT INTO users VALUES (?, ?, default, default)";
    private static final String SQL_INSERT_BUSINESS = "INSERT INTO businesses VALUES (?, default, default, default)";
    private static final String SQL_UPDATE_USER = "UPDATE USERS SET sharesLocation=? WHERE name=?";
    private static final String SQL_UPDATE_USER_DN = "UPDATE USERS SET displayName=? WHERE name=?";
    private static final String SQL_UPDATE_ACC_PW = "UPDATE ACCOUNTS SET password=? WHERE name=?";
    // Subscriptions
    private static final String SQL_SELECT_USER_SUBSCRIPTIONS = "SELECT * FROM user_subscriptions";
    private static final String SQL_SELECT_BUSINESS_SUBSCRIPTIONS = "SELECT * FROM business_subscriptions";
    private static final String SQL_SELECT_USER_SUBSCRIPTION = "SELECT us.* FROM users u JOIN user_subscriptions us ON us.id = u.subscriptionid WHERE u.name = ?";
    private static final String SQL_SELECT_BUSINESS_SUBSCRIPTION = "SELECT bs.* FROM businesses b JOIN business_subscriptions bs ON bs.id = b.subscriptionid WHERE b.name = ?";
    private static final String SQL_SELECT_BUSINESS_SUBSCRIPTION_INFO = "SELECT bs.ID, bs.NAME, b.LARGEPODSUSED, b.SMALLPODSUSED FROM businesses b JOIN business_subscriptions bs ON bs.id = b.subscriptionid WHERE b.name = ?";
    private static final String SQL_UPDATE_USER_SUBSCRIPTION = "UPDATE users SET subscriptionid = ? WHERE name = ?";
    private static final String SQL_UPDATE_BUSINESS_SUBSCRIPTION = "UPDATE businesses SET subscriptionid = ? WHERE name = ?";

    // Endpoints
    @Override
    // TODO: 21-11-2020 add endpoint visibility logic: users see only their endpoint and public endpoints and friend home endpoints(if sharing), companies see all endpoints but what if normal person needs to send package to other person ???
    public Set<ShortEndpoint> getEndpoints() { //will be short for the meantime
        Set<ShortEndpoint> endpoints = new HashSet<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_GET_ENDPOINTS)) {

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
        try {
            getEndpoint(id);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Set<ShortEndpoint> getFavoriteEndpoints(BaseAccount acc) {
        Set<ShortEndpoint> favouredTrips = new HashSet<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_FAVORITE_ENDPOINT)) {

            stmt.setString(1, acc.getUsername());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int id = rs.getInt("id");
                    favouredTrips.add(new ShortEndpoint(id, name));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot get all favorites.");
        }
        return favouredTrips;
    }

    @Override
    public void favoriteEndpoint(BaseAccount acc, int id) { // TODO: 20-11-2020:   add validation
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_FAVORITE_ENDPOINT)) {
            stmt.setString(1, acc.getUsername());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not favorite endpoint.");
        }
    }

    @Override
    public void unFavoriteEndpoint(BaseAccount acc, int id) {  // TODO: 20-11-2020: add validation (endpoint exists and that endpoint is favored)
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_DELETE_FAVORITE_ENDPOINT)) {
            stmt.setString(1, acc.getUsername());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not un favorite endpoint.");
        }
    }

    @Override
    public void addAccount(BaseAccount account) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_ACCOUNT)) {

            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());
            stmt.setString(3, account.getAddress());
            stmt.setInt(4, account.getHomeAddressEndpoint());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot add account!");
        }
    }

    @Override
    public void addUser(UserAccount user) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_USER)) {
            addAccount(user);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot add user!");
        }
    }

    @Override
    public void changePassword(BaseAccount acc, String newPW) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_ACC_PW)) {
            stmt.setString(1, acc.getUsername());
            stmt.setString(2, newPW);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not change password.");
        }
    }

    @Override
    public void addBusiness(BusinessAccount business) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_INSERT_BUSINESS)) {
            addAccount(business);
            stmt.setString(1, business.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot add business!");
        }
    }

    @Override
    public void setShareLocation(UserAccount user, boolean shareLocation) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_USER)) {

            stmt.setBoolean(1, shareLocation);
            stmt.setString(2, user.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not share location.");
        }
    }

    @Override
    public void setDisplayName(UserAccount acc, String displayName) {

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_USER_DN)) {
            stmt.setString(1, displayName);
            stmt.setString(2, acc.getUsername());
            acc.setDisplayName(displayName);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not update the display name");
        }
    }

    // Friends
    @Override
    public Set<UserAccount> getFriends(UserAccount user, Set<UserAccount> users) { // TODO: 20-11-2020 hacky?
        Set<UserAccount> friends = new HashSet<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_ALL_FRIENDS)) {
            stmt.setString(1, user.getUsername());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("friendName");
                    friends.add(
                            users.stream()
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

    public ShortEndpoint getShortEndpoint(int id) {
        Endpoint endpoint = getEndpoint(id);
        return new ShortEndpoint(endpoint.getId(), endpoint.getName());
    }

    // Travel / Delivery (packages)
    @Override
    public Set<Travel> getTravelHistory(UserAccount acc) {
        Set<Travel> travels = new HashSet<>();
        try (Connection conn = MarsConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_TRAVEL_HISTORY)) {
            stmt.setString(1, acc.getUsername());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int from = rs.getInt("from");
                    int destination = rs.getInt("destination");
                    String podType = rs.getString("podType");
                    String date = rs.getString("dateTime");
                    travels.add(new Travel(getShortEndpoint(from), getShortEndpoint(destination), PodType.enumOf(podType), date));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not add trip to DB.");
        }
        return travels;
    }

    @Override
    public void travel(UserAccount user, Travel travel) {
        try (Connection conn = MarsConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_TRAVEL)) {

            stmt.setInt(1, travel.getFrom().getId());
            stmt.setInt(2, travel.getDestination().getId());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, travel.getPodType().toString());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not add travel to DB");
        }
    }

    @Override
    public void cancelTravel(UserAccount user, int tripID) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_DELETE_TRAVEL)) {
            stmt.setString(1, user.getUsername());
            stmt.setInt(2, tripID);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not cancel travel/trip.");
        }
    }

    @Override
    public Set<Delivery> getDeliveries() {
        return Collections.emptySet();
    }

    @Override
    public int addDelivery(Delivery delivery) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_ADD_DELIVERY, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, delivery.getDeliveryType().toString());
            stmt.setInt(2, delivery.getSource().getId());
            stmt.setInt(3, delivery.getDestination().getId());
            stmt.setString(4, delivery.getSender());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getInt(1);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't add delivery!");
        }
    }

    @Override
    public Set<UserSubscription> getUserSubscriptions() {
        Set<UserSubscription> subscriptions = new HashSet<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_USER_SUBSCRIPTIONS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                subscriptions.add(getUserSubscription(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't get userSubscriptions");
        }
        return subscriptions;
    }

    @Override
    public Set<BusinessSubscription> getBusinessSubscriptions() {
        Set<BusinessSubscription> subscriptions = new HashSet<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_BUSINESS_SUBSCRIPTIONS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                subscriptions.add(getBusinessSubscription(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't get userSubscriptions");
        }
        return subscriptions;
    }

    @Override
    public UserSubscription getUserSubscription(UserAccount user) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_USER_SUBSCRIPTION)) {

            stmt.setString(1, user.getUsername());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return getUserSubscription(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't get user subscription");
        }
    }

    private UserSubscription getUserSubscription(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        boolean unlimitedTravels = rs.getBoolean("unlimitedTravels");
        boolean unlimitedPackages = rs.getBoolean("unlimitedPackages");
        return new UserSubscription(id, name, unlimitedTravels, unlimitedPackages);
    }

    private BusinessSubscription getBusinessSubscription(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int smallPodsDaily = rs.getInt("smallPodsDaily");
        int largePodsDaily = rs.getInt("largePodsDaily");
        int dedicatedPods = rs.getInt("dedicatedPods");
        int priorityLevel = rs.getInt("priorityLevel");
        return new BusinessSubscription(id, name, smallPodsDaily, largePodsDaily, dedicatedPods, priorityLevel);
    }

    @Override
    public BusinessSubscription getBusinessSubscription(BusinessAccount business) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_BUSINESS_SUBSCRIPTION)) {

            stmt.setString(1, business.getUsername());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return getBusinessSubscription(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't get business subscription");
        }
    }

    @Override
    public BusinessSubscriptionInfo getBusinessSubscriptionInfo(BusinessAccount business) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_BUSINESS_SUBSCRIPTION_INFO)) {
            stmt.setString(1, business.getUsername());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int smallPodsDaily = rs.getInt("smallPodsUsed");
                int largePodsDaily = rs.getInt("largePodsUsed");
                return new BusinessSubscriptionInfo(id, name, smallPodsDaily, largePodsDaily);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't get business subscription information");
        }
    }

    @Override
    public void setUserSubscription(UserAccount user, int subscriptionId) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_USER_SUBSCRIPTION)) {

            stmt.setInt(1, subscriptionId);
            stmt.setString(2, user.getUsername());
            stmt.execute();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't buy user subscription");
        }
    }

    @Override
    public void setBusinessSubscription(BusinessAccount business, int subscriptionId) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_BUSINESS_SUBSCRIPTION)) {

            stmt.setInt(1, subscriptionId);
            stmt.setString(2, business.getUsername());
            stmt.execute();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Can't buy business subscription");
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
