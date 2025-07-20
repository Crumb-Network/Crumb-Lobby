package me.kalbskinder.crumbLobby.database;

import me.kalbskinder.crumbLobby.utils.LocationHelper;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

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

    public void createLaunchpad(String location, String type) throws SQLException {
        String sql = "INSERT INTO launchpad(location, plate_type) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, location);
            statement.setString(2, type);
            statement.executeUpdate();
        }
    }

    public String getLaunchpadLocation(int id) throws SQLException {
        String sql = "SELECT location FROM launchpad WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("location");
                }
            }
        }
        return null;
    }

    public int getLaunchpadId(String location) throws SQLException {
        String sql = "SELECT id FROM launchpad WHERE location = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, location);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        return -1;
    }

    public void deleteLaunchpad(String location) throws SQLException {
        String sql = "DELETE FROM launchpad WHERE location = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, location);
            statement.executeUpdate();
        }
    }

    public void deleteLaunchpad(int id) throws SQLException {
        String sql = "DELETE FROM launchpad WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public Set<Location> getLaunchpadLocations() throws SQLException {
        String sql = "SELECT location FROM launchpad";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            Set<Location> locations = new HashSet<>();

            while (resultSet.next()) {
                String locString = resultSet.getString("location");
                if (locString == null) continue;
                Location location = LocationHelper.stringToLocation(locString);
                if (location == null) continue;
                locations.add(location);
            }
            return locations;
        }
    }

    public void updateLaunchpadType(int id, String type) throws SQLException {
        String sql = "UPDATE launchpad SET plate_type = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    public String getLaunchpadType(int id) throws SQLException {
        String sql = "SELECT plate_type FROM launchpad WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("plate_type");
                }
            }
        }
        return null;
    }
}
