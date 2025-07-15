package me.kalbskinder.crumbLobby.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Query {
    private final Connection connection;
    public Query(Connection connection) {
        this.connection = connection;
    }

    public void setSpawn(String location) throws SQLException {
        String sql = "REPLACE INTO spawn(id, location) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, 1); // ID fix auf 1
            statement.setString(2, location);
            statement.executeUpdate();
        }
    }

    public String getSpawn() throws SQLException {
        String sql = "SELECT location FROM spawn";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("location");
                }
            }
        }
        return null;
    }
}
