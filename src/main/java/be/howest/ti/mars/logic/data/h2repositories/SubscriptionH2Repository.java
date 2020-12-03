package be.howest.ti.mars.logic.data.h2repositories;

import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.controller.subscription.BusinessSubscription;
import be.howest.ti.mars.logic.controller.subscription.BusinessSubscriptionInfo;
import be.howest.ti.mars.logic.controller.subscription.UserSubscription;
import be.howest.ti.mars.logic.data.repositories.SubscriptionRepository;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import jdk.jshell.JShell;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubscriptionH2Repository implements SubscriptionRepository {
    private static final Logger LOGGER = Logger.getLogger(SubscriptionH2Repository.class.getName());

    // Subscriptions
    private static final String SQL_SELECT_USER_SUBSCRIPTIONS = "SELECT * FROM user_subscriptions";
    private static final String SQL_SELECT_BUSINESS_SUBSCRIPTIONS = "SELECT * FROM business_subscriptions";
    private static final String SQL_SELECT_USER_SUBSCRIPTION = "SELECT us.* FROM users u JOIN user_subscriptions us ON us.id = u.subscriptionid WHERE u.name = ?";
    private static final String SQL_SELECT_BUSINESS_SUBSCRIPTION = "SELECT bs.* FROM businesses b JOIN business_subscriptions bs ON bs.id = b.subscriptionid WHERE b.name = ?";
    private static final String SQL_SELECT_BUSINESS_SUBSCRIPTION_INFO = "SELECT bs.ID, bs.NAME, b.LARGEPODSUSED, b.SMALLPODSUSED FROM businesses b JOIN business_subscriptions bs ON bs.id = b.subscriptionid WHERE b.name = ?";
    private static final String SQL_UPDATE_USER_SUBSCRIPTION = "UPDATE users SET subscriptionid = ? WHERE name = ?";
    private static final String SQL_UPDATE_BUSINESS_SUBSCRIPTION = "UPDATE businesses SET subscriptionid = ? WHERE name = ?";
    private static final String SQL_UPDATE_BUSINESS_SUBSCRIPTION_INFO = "UPDATE businesses SET LARGEPODSUSED = ? AND SMALLPODSUSED = ? WHERE name = ?";
    private static final String SQL_UPDATE_BUSINESS_SUBSCRIPTION_INFO_SMALL = "UPDATE businesses SET SMALLPODSUSED = ? WHERE name = ?";
    private static final String SQL_UPDATE_BUSINESS_SUBSCRIPTION_INFO_LARGE = "UPDATE businesses SET LARGEPODSUSED = ? WHERE name = ?";


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
            throw new DatabaseException("Can't get businessSubscriptions");
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
    public void updateBusinessSubscription(boolean largePackage, BusinessAccount acc) {
        BusinessSubscriptionInfo currentInfo = getBusinessSubscriptionInfo(acc);
        int currentUsedPods;
        String sqlStatement;
        if (largePackage) {
            sqlStatement = SQL_UPDATE_BUSINESS_SUBSCRIPTION_INFO_LARGE;
            currentUsedPods = currentInfo.getLargePodsUsed();
        } else {
            sqlStatement = SQL_UPDATE_BUSINESS_SUBSCRIPTION_INFO_SMALL;
            currentUsedPods = currentInfo.getSmallPodsUsed();
        }

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlStatement)) {
            stmt.setInt(1, currentUsedPods + 1);
            stmt.setString(2, acc.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not update business subscription information");
        }
    }

    public void resetPods(BusinessAccount acc) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_BUSINESS_SUBSCRIPTION_INFO)) {
            stmt.setString(1, acc.getUsername());
            stmt.setInt(2, 0);
            stmt.setInt(3, 0);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not reset the daily-pods");
        }
    }

    @Override
    public void setUserSubscription(UserAccount user, int subscriptionId) {
        if (userSubscriptionExists(subscriptionId)){
            try (Connection con = MarsConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_USER_SUBSCRIPTION)) {

                stmt.setInt(1, subscriptionId);
                stmt.setString(2, user.getUsername());
                stmt.execute();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                throw new DatabaseException("Can't buy user subscription");
            }
        }else{
            throw  new DatabaseException("Could not find a subscription with given id");
        }
    }

    @Override
    public void setBusinessSubscription(BusinessAccount business, int subscriptionId) {
        if (businessSubscriptionExists(subscriptionId)){
            try (Connection con = MarsConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_BUSINESS_SUBSCRIPTION)) {

                stmt.setInt(1, subscriptionId);
                stmt.setString(2, business.getUsername());
                stmt.execute();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                throw new DatabaseException("Can't buy business subscription");
            }
        }else{
            throw  new DatabaseException("Could not find a subscription with given id");
        }
    }

    private boolean businessSubscriptionExists(int id){
        return getBusinessSubscriptions().stream().anyMatch(subscription -> subscription.getId() == id);
    }

    private boolean userSubscriptionExists(int id){
        return getUserSubscriptions().stream().anyMatch(subscription -> subscription.getId() == id);
    }

}
