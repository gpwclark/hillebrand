package org.uant.textservice.db;

import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import java.sql.*;

import org.uant.textservice.db.TestEmailGenerator;


public final class ResourceDb {
    DataSource ds;

    public ResourceDb(DataSource ds){
        this.ds = ds;
    }

    public boolean isValidResource(String resourceHandle) {
        boolean isValidResource = false;
        try (
              Connection conn = ds.getConnection();
              PreparedStatement statement = conn.prepareStatement("SELECT email FROM resources WHERE email=?");
            ) {
            statement.setString(1, resourceHandle);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    if (resourceHandle.equals(resultSet.getString("email")))
                        isValidResource = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isValidResource;
    }

    public String getResourceMessage(String resourceHandle) {
        boolean isValid = isValidResource(resourceHandle);
        if (isValid)
            //TODO actual DB lookup...
            return "all orders shipped";
        else
            return "invalid resource";
    }
}
