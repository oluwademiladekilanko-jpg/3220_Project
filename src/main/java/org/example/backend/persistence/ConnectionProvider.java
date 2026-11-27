package org.example.backend.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/** Abstraction over "where do database connections come from" so nothing else depends on a URL or driver. */
public interface ConnectionProvider {
    Connection getConnection() throws SQLException;
}
