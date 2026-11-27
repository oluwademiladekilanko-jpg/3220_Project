package org.example.backend.persistence;

import org.example.Objects.Doctor;
import org.example.Objects.Patient;
import org.example.Objects.Receptionist;
import org.example.Objects.Role;
import org.example.Objects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteUserRepository implements UserRepository {
    private final ConnectionProvider connections;

    public SQLiteUserRepository(ConnectionProvider connections) {
        this.connections = connections;
    }

    @Override
    public Optional<User> findByEmail(Role role, String email) {
        // table name comes from the Role enum, never from user input
        String sql = "SELECT * FROM " + role.getTableName() + " WHERE email = ?";
        try (Connection con = connections.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                switch (role) {
                    case DOCTOR: return Optional.of(RowMapper.doctor(rs, ""));
                    case RECEPTIONIST: return Optional.of(RowMapper.receptionist(rs, ""));
                    default: return Optional.of(RowMapper.patient(rs, ""));
                }
            }
        } catch (SQLException e) {
            System.err.println("findByEmail failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public SaveResult save(User user) {
        String sql;
        Object[] params;
        switch (user.getRole()) {
            case DOCTOR: {
                Doctor d = (Doctor) user;
                sql = "INSERT INTO doctor (name, email, password, phone, field, emrgphone) VALUES (?, ?, ?, ?, ?, ?)";
                params = new Object[]{d.getName(), d.getEmail(), d.getPassword(), d.getPhone(), d.getField(), d.getEmrgphone()};
                break;
            }
            case RECEPTIONIST: {
                Receptionist r = (Receptionist) user;
                sql = "INSERT INTO receptionist (name, email, password, phone) VALUES (?, ?, ?, ?)";
                params = new Object[]{r.getName(), r.getEmail(), r.getPassword(), r.getPhone()};
                break;
            }
            default: {
                Patient p = (Patient) user;
                sql = "INSERT INTO patient (name, email, password, phone, illness, symptoms) VALUES (?, ?, ?, ?, ?, ?)";
                params = new Object[]{p.getName(), p.getEmail(), p.getPassword(), p.getPhone(), p.getIllness(), p.getSymptoms()};
            }
        }

        try (Connection con = connections.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
            return SaveResult.SAVED;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) return SaveResult.DUPLICATE_EMAIL;
            System.err.println("save failed: " + e.getMessage());
            return SaveResult.FAILED;
        }
    }

    @Override
    public void updatePassword(Role role, int userId, String newHash) {
        String sql = "UPDATE " + role.getTableName() + " SET password = ? WHERE id = ?";
        try (Connection con = connections.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, newHash);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("updatePassword failed: " + e.getMessage());
        }
    }

    @Override
    public List<Patient> findAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT id, illness, symptoms, name, email, password, phone FROM patient";
        try (Connection con = connections.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                patients.add(RowMapper.patient(rs, ""));
            }
        } catch (SQLException e) {
            System.err.println("findAllPatients failed: " + e.getMessage());
        }
        return patients;
    }

    @Override
    public List<Doctor> findDoctors(String field) {
        List<Doctor> doctors = new ArrayList<>();
        boolean filtered = field != null && !field.isEmpty();
        String sql = filtered
                ? "SELECT * FROM doctor WHERE field = ? ORDER BY name"
                : "SELECT * FROM doctor ORDER BY name";
        try (Connection con = connections.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            if (filtered) stmt.setString(1, field);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(RowMapper.doctor(rs, ""));
                }
            }
        } catch (SQLException e) {
            System.err.println("findDoctors failed: " + e.getMessage());
        }
        return doctors;
    }

    @Override
    public List<String> findDoctorFields() {
        List<String> fields = new ArrayList<>();
        String sql = "SELECT DISTINCT field FROM doctor ORDER BY field";
        try (Connection con = connections.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fields.add(rs.getString("field"));
            }
        } catch (SQLException e) {
            System.err.println("findDoctorFields failed: " + e.getMessage());
        }
        return fields;
    }
}
