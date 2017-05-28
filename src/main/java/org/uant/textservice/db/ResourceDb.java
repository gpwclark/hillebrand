package org.uant.textservice.db;

import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import java.sql.*;

import org.uant.textservice.db.TestEmailGenerator;


public final class ResourceDb {
    TestEmailGenerator testEmailGen = new TestEmailGenerator();
    final Set<String> validCustomers = new HashSet<String>(testEmailGen.getTestEmails());
    DataSource ds;

    public ResourceDb(DataSource ds){
        this.ds = ds;
    }

    public boolean isValidCustomer(String customerEmail) {
        boolean isValidCustomer = false;
        try (
              Connection conn = ds.getConnection();
              PreparedStatement statement = conn.prepareStatement("SELECT email FROM customers WHERE email=?");
            ) {
            statement.setString(1, customerEmail);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    if (customerEmail.equals(resultSet.getString("email")))
                        isValidCustomer = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValidCustomer;
    }

    public String getCustomerMessage(String customerEmail) {
        boolean isValid = isValidCustomer(customerEmail);
        if (isValid)
            //TODO actual DB lookup...
            return "all orders shipped";
        else
            return "invalid customer";
    }
}
