package me.kalbskinder.crumbLobby.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private final Connection connection;

    public Database (String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        Statement statement = connection.createStatement();
        statement.execute("""
            CREATE TABLE IF NOT EXISTS spawn (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                location TEXT NOT NULL
            )
        """);

        statement.execute("""
            CREATE TABLE IF NOT EXISTS launchpad (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                location TEXT NOT NULL,
                plate_type TEXT NOT NULL
            )
        """);

        statement.close();
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
