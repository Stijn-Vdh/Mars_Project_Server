package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.data.MarsConnection;
import be.howest.ti.mars.logic.data.MarsH2Repository;
import be.howest.ti.mars.logic.data.repoInterfaces.AccountsRepoInt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountRepository implements AccountsRepoInt {
    private static final Logger LOGGER = Logger.getLogger(MarsH2Repository.class.getName());
    // Accounts SQL QUERIES
    private static final String SQL_SELECT_ACCOUNTS = "SELECT * FROM ACCOUNTS";
    private static final String SQL_SELECT_USERS = "SELECT * FROM users u JOIN accounts a ON a.name = u.name";
    private static final String SQL_SELECT_BUSINESSES = "SELECT * FROM users u join businesses b on b.name = u.name";
    private static final String SQL_INSERT_ACCOUNT = "INSERT INTO accounts VALUES (?, ?, ?, ?)";
    private static final String SQL_INSERT_USER = "INSERT INTO users VALUES (?, ?, default, default)";
    private static final String SQL_INSERT_BUSINESS = "INSERT INTO businesses VALUES (?, default, default, default)";
    private static final String SQL_UPDATE_USER = "UPDATE USERS SET sharesLocation=? WHERE name=?";
    private static final String SQL_UPDATE_USER_DN = "UPDATE USERS SET displayName=? WHERE name=?";
    private static final String SQL_UPDATE_ACC_PW = "UPDATE ACCOUNTS SET password=? WHERE name=?";

    private static final String PASSWORD = "password";
    public static final String HOME_ADDRESS = "homeAddress";
    public static final String HOME_ENDPOINT_ID = "homeEndpointId";

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
    public Set<BaseAccount> getAccounts() {
        Set<BaseAccount> accounts = new HashSet<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_ACCOUNTS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString(PASSWORD);
                String address = rs.getString(HOME_ADDRESS);
                int endpointId = rs.getInt(HOME_ENDPOINT_ID);
                accounts.add(new BaseAccount(name, password, address, endpointId));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot get all accounts.");
        }
        return accounts;
    }

    @Override
    public Set<UserAccount> getUserAccounts() {
        Set<UserAccount> accounts = new HashSet<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_USERS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString(PASSWORD);
                String address = rs.getString(HOME_ADDRESS);
                boolean sharesLocation = rs.getBoolean("sharesLocation");
                String displayName = rs.getString("displayName");
                int endpointId = rs.getInt(HOME_ENDPOINT_ID);
                int subscriptionId = rs.getInt("subscriptionId");
                accounts.add(new UserAccount(name, password, address, endpointId, displayName, sharesLocation, subscriptionId));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot get all userAccounts.");
        }
        return accounts;
    }

    @Override
    public Set<BusinessAccount> getBusinessAccounts() {
        Set<BusinessAccount> accounts = new HashSet<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_BUSINESSES);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString(PASSWORD);
                String address = rs.getString(HOME_ADDRESS);
                int endpointId = rs.getInt(HOME_ENDPOINT_ID);
                int subscriptionId = rs.getInt("subscriptionId");
                int smallPodsUsed = rs.getInt("smallPodsUsed");
                int largePodsUsed = rs.getInt("largePodsUsed");
                accounts.add(new BusinessAccount(name, password, address, endpointId, subscriptionId, smallPodsUsed, largePodsUsed));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot get all businessAccounts");
        }
        return accounts;
    }

    // User
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
            stmt.setString(1, newPW);
            stmt.setString(2, acc.getUsername());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not change password.");
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
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not update the display name");
        }
    }

    // Business
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

}
