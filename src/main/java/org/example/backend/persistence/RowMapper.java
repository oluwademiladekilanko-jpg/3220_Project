package org.example.backend.persistence;

import org.example.Objects.Doctor;
import org.example.Objects.Patient;
import org.example.Objects.Receptionist;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Package-private helper: turns result-set rows into domain objects. Prefix lets joins alias columns (p_, d_). */
final class RowMapper {
    private RowMapper() { }

    static Patient patient(ResultSet rs, String prefix) throws SQLException {
        return new Patient(
                rs.getInt(prefix + "id"),
                rs.getString(prefix + "illness"),
                rs.getString(prefix + "symptoms"),
                rs.getString(prefix + "name"),
                rs.getString(prefix + "email"),
                rs.getString(prefix + "password"),
                rs.getLong(prefix + "phone"));
    }

    static Doctor doctor(ResultSet rs, String prefix) throws SQLException {
        return new Doctor(
                rs.getString(prefix + "name"),
                rs.getString(prefix + "email"),
                rs.getString(prefix + "password"),
                rs.getString(prefix + "field"),
                rs.getLong(prefix + "phone"),
                rs.getLong(prefix + "emrgphone"),
                rs.getInt(prefix + "id"));
    }

    static Receptionist receptionist(ResultSet rs, String prefix) throws SQLException {
        return new Receptionist(
                rs.getInt(prefix + "id"),
                rs.getString(prefix + "name"),
                rs.getString(prefix + "email"),
                rs.getString(prefix + "password"),
                rs.getLong(prefix + "phone"));
    }
}
