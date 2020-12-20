package be.howest.ti.mars.logic.data.h2repositories;

import be.howest.ti.mars.logic.controller.converters.Delivery;
import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.enums.DeliveryType;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.controller.exceptions.EntityNotFoundException;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repositories.DeliveriesRepository;
import be.howest.ti.mars.logic.data.repositories.EndpointsRepository;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeliveriesH2Repository implements DeliveriesRepository {
    private static final Logger LOGGER = Logger.getLogger(SubscriptionH2Repository.class.getName());

    // Deliveries SQL Queries
    private static final String SQL_ADD_DELIVERY = "INSERT INTO DELIVERIES VALUES(DEFAULT, ?, ?, ?, DEFAULT, ?)";
    private static final String SQL_SELECT_DELIVERIES = "SELECT * FROM DELIVERIES WHERE sender=?";
    private static final String SQL_SELECT_DELIVERY = "SELECT * FROM DELIVERIES WHERE sender=? AND id=?";

    public static final String DESTINATION = "destination";
    public static final String DATE_TIME = "dateTime";

    private Delivery createDelivery(ResultSet rs) throws SQLException {
        EndpointsRepository repo = Repositories.getEndpointsRepo();
        int id = rs.getInt("id");
        String type = rs.getString("deliveryType");
        int source = rs.getInt("from");
        int destination = rs.getInt(DESTINATION);
        String date = rs.getString(DATE_TIME);
        String sender = rs.getString("sender");
        return new Delivery(id, DeliveryType.enumOf(type), repo.getShortEndpoint(source), repo.getShortEndpoint(destination), date, sender);
    }

    @Override
    public List<Delivery> getDeliveries(BusinessAccount acc) {

        List<Delivery> deliveries = new LinkedList<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_DELIVERIES)) {
            stmt.setString(1, acc.getUsername());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    deliveries.add(createDelivery(rs));
                }
            }
            return deliveries;

        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not get deliveries from DB");
        }
    }

    @Override
    public int addDelivery(Delivery delivery) {
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_ADD_DELIVERY, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, delivery.getDeliveryType().toString());
            stmt.setInt(2, delivery.getFrom().getId());
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
    public Object getDeliveryInformation(BaseAccount acc, int id) {

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_DELIVERY)) {
            stmt.setString(1, acc.getUsername());
            stmt.setInt(2, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()){
                    return createDelivery(rs);
                }else{
                    throw new EntityNotFoundException("Could not get delivery information.");
                }
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not get deliveries from DB");
        }
    }
}
