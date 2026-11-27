package org.example.backend.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnectionProvider implements ConnectionProvider {
    private final String url;

    public SQLiteConnectionProvider(String url) {
        this.url = url;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(url);
        try (Statement stmt = con.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return con;
    }
}
