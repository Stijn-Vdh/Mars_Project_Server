package be.howest.ti.mars.logic.data.repositories;

import be.howest.ti.mars.logic.controller.accounts.BaseAccount;
import be.howest.ti.mars.logic.controller.exceptions.DatabaseException;
import be.howest.ti.mars.logic.controller.exceptions.EntityNotFoundException;
import be.howest.ti.mars.logic.data.util.MarsConnection;
import be.howest.ti.mars.logic.data.MarsH2Repository;
import be.howest.ti.mars.logic.data.repoInterfaces.ReportsRepoInt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportsRepository implements ReportsRepoInt {
    private static final Logger LOGGER = Logger.getLogger(MarsH2Repository.class.getName());

    // Reports SQL QUERIES
    private static final String SQL_GET_REPORT_SECTIONS = "SELECT * FROM REPORT_SECTIONS";
    private static final String SQL_INSERT_REPORTS = "INSERT INTO REPORTS VALUES(DEFAULT, ?, ?, ?)";

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
