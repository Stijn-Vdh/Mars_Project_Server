package be.howest.ti.mars.logic.data.h2repositories;

import be.howest.ti.mars.logic.controller.Coordinate;
import be.howest.ti.mars.logic.controller.Endpoint;
import be.howest.ti.mars.logic.controller.accounts.UserAccount;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.controller.exceptions.EndpointException;
import be.howest.ti.mars.logic.controller.exceptions.MarsIllegalArgumentException;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.EndpointsRepository;
import be.howest.ti.mars.logic.data.util.MarsConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EndpointsH2Repository implements EndpointsRepository {
    private static final Logger LOGGER = Logger.getLogger(SubscriptionH2Repository.class.getName());

    // Endpoints
    private static final String SQL_GET_ENDPOINT = "SELECT * FROM ENDPOINTS WHERE ID = ?";
    private static final String SQL_GET_ENDPOINTS = "SELECT * FROM ENDPOINTS";
    private static final String SQL_INSERT_ENDPOINT = "INSERT INTO ENDPOINTS(name) VALUES(?)";
    private static final String SQL_UPDATE_ENDPOINT_PRIVATE = "UPDATE ENDPOINTS set private = true where id = ?";
    private static final String SQL_GET_PACKAGE_ENDPOINTS = "SELECT * FROM ENDPOINTS WHERE private = true";
    private static final String SQL_GET_TRAVEL_ENDPOINTS =
            "SELECT * FROM ENDPOINTS where private = false or id in (" +
                    "SELECT homeEndpointId FROM users u JOIN accounts a ON a.name = u.name where shareslocation = true and u.name in (" +
                    "           select friendname from friends where username =  ? ) " +
                    ") or id = ?";

    private boolean endpointExistsByName(String name) {
        return getEndpoints().stream().anyMatch(endpoint -> endpoint.getName().equals(name));
    }

    @Override
    public ShortEndpoint getShortEndpoint(int id) {
        Endpoint endpoint = Repositories.getEndpointsRepo().getEndpoint(id);
        return new ShortEndpoint(endpoint.getId(), endpoint.getName());
    }

    @Override
    public boolean endpointExists(int id) {
        return getEndpoints().stream().anyMatch(endpoint -> endpoint.getId() == id);
    }

    @Override
    public Set<ShortEndpoint> getEndpoints() {
        return getEndpoints(SQL_GET_ENDPOINTS);
    }

    @Override
    public void addEndpoint(String endpoint) {
        if (!endpointExistsByName(endpoint)) {
            try (Connection con = MarsConnection.getConnection();
                 PreparedStatement stmt = con.prepareStatement(SQL_INSERT_ENDPOINT)) {
                stmt.setString(1, endpoint);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new DatabaseException("Can't add endpoint!");
            }
        } else {
            throw new MarsIllegalArgumentException("Endpoint already exists!");
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
                    return getEndpoint(rs);
                } else {
                    throw new EndpointException("Endpoint with ID (" + id + ") doesn't exist!");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot retrieve endpoint with id: " + id + "!");
        }
    }

    private Endpoint getEndpoint(ResultSet rs) throws SQLException {
        return new Endpoint(rs.getInt("id"), rs.getString("name"), rs.getBoolean("available"),
                new Coordinate(rs.getDouble("longitude"), rs.getDouble("latitude")), rs.getBoolean("private"));
    }


    @Override
    public void turnEndpointPrivate(int id) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_ENDPOINT_PRIVATE)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("Can't add endpoint!");
        }

    }

    @Override
    public Set<Endpoint> getTravelEndpoints(UserAccount user) {
        Set<Endpoint> endpoints = new HashSet<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_GET_TRAVEL_ENDPOINTS)) {
            stmt.setString(1, user.getUsername());
            stmt.setInt(2, user.getHomeAddressEndpoint());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    endpoints.add(getEndpoint(rs));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Cannot retrieve endpoints");
        }
        return endpoints;
    }

    private Set<ShortEndpoint> getEndpoints(String sqlQuery) {
        Set<ShortEndpoint> endpoints = new HashSet<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlQuery)) {
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
    public Set<ShortEndpoint> getPackageEndpoints() {
        return getEndpoints(SQL_GET_PACKAGE_ENDPOINTS);
    }
}
