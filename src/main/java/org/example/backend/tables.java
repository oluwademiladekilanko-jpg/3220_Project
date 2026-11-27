package org.example.backend;

import org.example.backend.service.AppContext;

import java.sql.Connection;
import java.sql.SQLException;

/** Transitional facade: same API as before, now delegating to the persistence layer. */
public class tables {
    public static final String url = AppContext.DEFAULT_URL;

    public static Connection connection() {
        try {
            return AppContext.get().connections().getConnection();
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            return null;
        }
    }

    public static void createTables() {
        AppContext.get().schema().initialize();
    }
}
