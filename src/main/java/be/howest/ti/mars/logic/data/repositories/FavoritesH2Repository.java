package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.controller.exceptions.MarsIllegalArgumentException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import be.howest.ti.mars.logic.data.repoInterfaces.FavoritesRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FavoritesH2Repository implements FavoritesRepository {
    private static final Logger LOGGER = Logger.getLogger(SubscriptionH2Repository.class.getName());

    // Favorites SQL QUERIES
    private static final String SQL_DELETE_FAVORITE_ENDPOINT = "DELETE FROM favorite_endpoints WHERE ACCOUNTNAME=? AND ENDPOINTID=?;";
    private static final String SQL_INSERT_FAVORITE_ENDPOINT = "INSERT INTO favorite_endpoints VALUES (?, ?)";
    private static final String SQL_SELECT_FAVORITE_ENDPOINT = "SELECT * FROM favorite_endpoints fe JOIN endpoints e ON fe.endpointid = e.id WHERE accountname = ?";

    private static final String ERROR_MESSAGE_FAVOURITE = "Could not favorite endpoint.";
    private static final String ERROR_MESSAGE_UNFAVORITE = "Could not un favorite endpoint.";

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
    public void favoriteEndpoint(BaseAccount acc, int id) {
        if (Repositories.getEndpointsRepo().endpointExists(id) && !isFavoured(acc, id)){
            try (Connection con = MarsConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(SQL_INSERT_FAVORITE_ENDPOINT)) {
                stmt.setString(1, acc.getUsername());
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                throw new DatabaseException(ERROR_MESSAGE_FAVOURITE);
            }
        }else{
            throw new MarsIllegalArgumentException(ERROR_MESSAGE_FAVOURITE);
        }
    }

    @Override
    public void unFavoriteEndpoint(BaseAccount acc, int id) {
        if (Repositories.getEndpointsRepo().endpointExists(id) && isFavoured(acc, id)){
            try (Connection con = MarsConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(SQL_DELETE_FAVORITE_ENDPOINT)) {
                stmt.setString(1, acc.getUsername());
                stmt.setInt(2, id);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                throw new DatabaseException(ERROR_MESSAGE_UNFAVORITE);
            }
        }else{
            throw new MarsIllegalArgumentException(ERROR_MESSAGE_UNFAVORITE);
        }
    }

    private boolean isFavoured(BaseAccount acc, int id) {
        boolean res = false;
        for(ShortEndpoint endpoint : getFavoriteEndpoints(acc)){
            if (endpoint.getId() == id) {
                res = true;
                break;
            }
        }
        return res;
    }
}
