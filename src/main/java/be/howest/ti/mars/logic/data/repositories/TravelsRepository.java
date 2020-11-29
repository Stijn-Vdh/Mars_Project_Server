package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.Travel;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.enums.PodType;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repoInterfaces.EndpointsRepoInt;
import be.howest.ti.mars.logic.data.repoInterfaces.TravelsRepoInt;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TravelsRepository implements TravelsRepoInt {

    private static final Logger LOGGER = Logger.getLogger(SubscriptionRepository.class.getName());

    // Travels SQL QUERIES
    private static final String SQL_INSERT_TRAVEL = "INSERT INTO TRAVELS VALUES(default, ?, ?, ?, DEFAULT, ?, NULL)";
    private static final String SQL_SELECT_TRAVEL_HISTORY = "SELECT * FROM TRAVELS t WHERE userName=? ";
    private static final String SQL_DELETE_TRAVEL = "DELETE FROM TRAVELS WHERE userName=? AND ID=?";

    public static final String DESTINATION = "destination";
    public static final String DATE_TIME = "dateTime";



    @Override
    public List<Travel> getTravelHistory(UserAccount acc) {
        EndpointsRepoInt repo = Repositories.getEndpointsRepo();
        List<Travel> travels = new LinkedList<>();
        try (Connection conn = MarsConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_TRAVEL_HISTORY)) {
            stmt.setString(1, acc.getUsername());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int from = rs.getInt("from");
                    int destination = rs.getInt(DESTINATION);
                    String podType = rs.getString("podType");
                    String date = rs.getString(DATE_TIME);
                    travels.add(new Travel(id, repo.getShortEndpoint(from), repo.getShortEndpoint(destination), PodType.enumOf(podType), date));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not add trip to DB.");
        }
        return travels;
    }

    @Override
    public int travel(UserAccount user, Travel travel) {
        try (Connection conn = MarsConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_TRAVEL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, travel.getFrom().getId());
            stmt.setInt(2, travel.getDestination().getId());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, travel.getPodType().toString());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getInt(1);
            }
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
}
