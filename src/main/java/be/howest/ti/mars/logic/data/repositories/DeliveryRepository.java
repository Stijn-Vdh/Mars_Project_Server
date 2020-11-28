package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.Delivery;
import be.howest.ti.mars.logic.controller.Endpoint;
import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.accounts.BusinessAccount;
import be.howest.ti.mars.logic.controller.converters.ShortEndpoint;
import be.howest.ti.mars.logic.controller.enums.DeliveryType;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.data.MarsConnection;
import be.howest.ti.mars.logic.data.MarsH2Repository;
import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.data.repoInterfaces.DeliveriesRepoInt;
import be.howest.ti.mars.logic.data.repoInterfaces.EndpointsRepoInt;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeliveryRepository implements DeliveriesRepoInt {
    private static final Logger LOGGER = Logger.getLogger(MarsH2Repository.class.getName());

    // Deliveries SQL Queries
    private static final String SQL_ADD_DELIVERY = "INSERT INTO DELIVERIES VALUES(DEFAULT, ?, ?, ?, DEFAULT, ?)";
    private static final String SQL_SELECT_DELIVERIES = "SELECT * FROM DELIVERIES WHERE sender=?";
    private static final String SQL_SELECT_DELIVERY = "SELECT * FROM DELIVERIES WHERE sender=? AND id=?";

    public static final String DESTINATION = "destination";
    public static final String DATE_TIME = "dateTime";

    private EndpointsRepoInt repo = Repositories.getEndpointsRepoInt();

    @Override
    public List<Delivery> getDeliveries(BusinessAccount acc) {

        List<Delivery> deliveries = new LinkedList<>();

        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_DELIVERIES)) {
            stmt.setString(1, acc.getUsername());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String type = rs.getString("deliveryType");
                    int source = rs.getInt("from");
                    int destination = rs.getInt(DESTINATION);
                    String date = rs.getString(DATE_TIME);
                    String sender = rs.getString("sender");

                    Delivery delivery = new Delivery(id, DeliveryType.enumOf(type), repo.getShortEndpoint(source), repo.getShortEndpoint(destination), date, sender);
                    deliveries.add(delivery);
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
    public Object getDeliveryInformation(BaseAccount acc, int id) {
        Delivery delivery = null;
        try (Connection con = MarsConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_SELECT_DELIVERY)) {
            stmt.setString(1, acc.getUsername());
            stmt.setInt(2, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int deliveryId = rs.getInt("id");
                    String type = rs.getString("deliveryType");
                    int source = rs.getInt("from");
                    int destination = rs.getInt(DESTINATION);
                    String date = rs.getString(DATE_TIME);
                    String sender = rs.getString("sender");


                    delivery = new Delivery(deliveryId, DeliveryType.enumOf(type), repo.getShortEndpoint(source), repo.getShortEndpoint(destination), date, sender);
                }
            }
            return delivery;

        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            throw new DatabaseException("Could not get deliveries from DB");
        }
    }
}
