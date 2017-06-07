package org.uant.textservice.db;

import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import java.sql.*;

public final class ResourceDb implements ResourceDriver{
    DataSource ds;

    public ResourceDb(DataSource ds){
        this.ds = ds;
    }

    public boolean isValidResource(String resourceHandle) {
        boolean isValidResource = false;
        try (
              Connection conn = ds.getConnection();
              PreparedStatement statement = conn.prepareStatement("SELECT resource FROM resources WHERE resource=?");
            ) {
            statement.setString(1, resourceHandle);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    if (resourceHandle.equals(resultSet.getString("resource")))
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
            return "all orders shipped";
        else
            return "invalid resource";
    }
}
