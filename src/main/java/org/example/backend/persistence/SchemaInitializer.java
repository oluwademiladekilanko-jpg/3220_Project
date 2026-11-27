package org.example.backend.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Creates the tables and constraints. Replaces tables.createTables(). */
public class SchemaInitializer {
    private final ConnectionProvider connections;

    public SchemaInitializer(ConnectionProvider connections) {
        this.connections = connections;
    }

    public void initialize() {
        String[] ddl = {
            "CREATE TABLE IF NOT EXISTS patient ("
                + " id INTEGER PRIMARY KEY, name TEXT NOT NULL, illness TEXT NOT NULL, symptoms TEXT,"
                + " email TEXT UNIQUE NOT NULL, password TEXT NOT NULL, phone INT NOT NULL)",
            "CREATE TABLE IF NOT EXISTS receptionist ("
                + " id INTEGER PRIMARY KEY, name TEXT NOT NULL,"
                + " email TEXT UNIQUE NOT NULL, password TEXT NOT NULL, phone INT NOT NULL)",
            "CREATE TABLE IF NOT EXISTS doctor ("
                + " id INTEGER PRIMARY KEY, name TEXT NOT NULL, field TEXT NOT NULL,"
                + " email TEXT UNIQUE NOT NULL, password TEXT NOT NULL, phone INT NOT NULL, emrgphone INT NOT NULL)",
            "CREATE TABLE IF NOT EXISTS appointment ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT, p_id INTEGER NOT NULL, d_id INTEGER NOT NULL,"
                + " date TEXT NOT NULL, location TEXT NOT NULL,"
                + " FOREIGN KEY (p_id) REFERENCES patient(id), FOREIGN KEY (d_id) REFERENCES doctor(id))",
            "CREATE TABLE IF NOT EXISTS availability ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT, d_id INTEGER NOT NULL, slot TEXT NOT NULL,"
                + " UNIQUE (d_id, slot), FOREIGN KEY (d_id) REFERENCES doctor(id))"
        };

        try (Connection con = connections.getConnection(); Statement stmt = con.createStatement()) {
            for (String sql : ddl) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return;
        }

        // Business rule enforced by the database itself: a doctor cannot have two appointments at the same time.
        // Separate step so an existing database containing duplicates does not stop startup.
        try (Connection con = connections.getConnection(); Statement stmt = con.createStatement()) {
            stmt.execute("CREATE UNIQUE INDEX IF NOT EXISTS idx_appointment_doctor_slot ON appointment(d_id, date)");
        } catch (SQLException e) {
            System.err.println("Could not add double-booking constraint (existing duplicate rows?): " + e.getMessage());
        }
        System.out.println("Database and tables ready.");
    }
}
