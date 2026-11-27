package org.example.backend.persistence;

import org.example.Objects.Appointment;
import org.example.Objects.TimeSlot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteAppointmentRepository implements AppointmentRepository {
    private static final String BASE_SELECT =
            "SELECT a.id AS a_id, a.date AS a_date, a.location AS a_location, "
            + "p.id AS p_id, p.name AS p_name, p.email AS p_email, p.password AS p_password, p.phone AS p_phone, "
            + "p.illness AS p_illness, p.symptoms AS p_symptoms, "
            + "d.id AS d_id, d.name AS d_name, d.email AS d_email, d.password AS d_password, d.phone AS d_phone, "
            + "d.field AS d_field, d.emrgphone AS d_emrgphone "
            + "FROM appointment a JOIN patient p ON p.id = a.p_id JOIN doctor d ON d.id = a.d_id ";

    private final ConnectionProvider connections;

    public SQLiteAppointmentRepository(ConnectionProvider connections) {
        this.connections = connections;
    }

    @Override
    public boolean insertIfSlotFree(int patientId, int doctorId, LocalDateTime slot, String location) {
        String sql = "INSERT INTO appointment (p_id, d_id, date, location) "
                + "SELECT ?, ?, ?, ? "
                + "WHERE EXISTS (SELECT 1 FROM availability WHERE d_id = ? AND slot = ?) "
                + "AND NOT EXISTS (SELECT 1 FROM appointment WHERE d_id = ? AND date = ?)";
        String text = slot.format(TimeSlot.FORMAT);
        try (Connection con = connections.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            stmt.setInt(2, doctorId);
            stmt.setString(3, text);
            stmt.setString(4, location);
            stmt.setInt(5, doctorId);
            stmt.setString(6, text);
            stmt.setInt(7, doctorId);
            stmt.setString(8, text);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("insertIfSlotFree failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int appointmentId) {
        String sql = "DELETE FROM appointment WHERE id = ?";
        try (Connection con = connections.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("deleteById failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Appointment> findById(int appointmentId) {
        List<Appointment> found = query(BASE_SELECT + "WHERE a.id = ?", appointmentId);
        return found.isEmpty() ? Optional.<Appointment>empty() : Optional.of(found.get(0));
    }

    @Override
    public List<Appointment> findByPatient(int patientId) {
        return query(BASE_SELECT + "WHERE a.p_id = ? ORDER BY a.date", patientId);
    }

    @Override
    public List<Appointment> findAll() {
        return query(BASE_SELECT + "ORDER BY a.date");
    }

    private List<Appointment> query(String sql, Integer... params) {
        List<Appointment> result = new ArrayList<>();
        try (Connection con = connections.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setInt(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new Appointment(
                            rs.getInt("a_id"),
                            RowMapper.patient(rs, "p_"),
                            RowMapper.doctor(rs, "d_"),
                            TimeSlot.parse(rs.getString("a_date")),
                            rs.getString("a_location")));
                }
            }
        } catch (SQLException e) {
            System.err.println("appointment query failed: " + e.getMessage());
        }
        return result;
    }
}
