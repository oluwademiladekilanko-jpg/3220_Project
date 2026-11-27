package org.example.backend.persistence;

import org.example.Objects.TimeSlot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLiteAvailabilityRepository implements AvailabilityRepository {
    private final ConnectionProvider connections;

    public SQLiteAvailabilityRepository(ConnectionProvider connections) {
        this.connections = connections;
    }

    @Override
    public List<TimeSlot> findOpenSlots(int doctorId, LocalDateTime after) {
        String sql = "SELECT slot FROM availability WHERE d_id = ? AND slot > ? "
                + "AND slot NOT IN (SELECT date FROM appointment WHERE d_id = ?) ORDER BY slot";
        List<TimeSlot> slots = new ArrayList<>();
        try (Connection con = connections.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            stmt.setString(2, after.format(TimeSlot.FORMAT));
            stmt.setInt(3, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(new TimeSlot(doctorId, TimeSlot.parse(rs.getString("slot")), false));
                }
            }
        } catch (SQLException e) {
            System.err.println("findOpenSlots failed: " + e.getMessage());
        }
        return slots;
    }

    @Override
    public List<TimeSlot> findAllSlots(int doctorId) {
        String sql = "SELECT slot, EXISTS(SELECT 1 FROM appointment a WHERE a.d_id = availability.d_id "
                + "AND a.date = availability.slot) AS booked FROM availability WHERE d_id = ? ORDER BY slot";
        List<TimeSlot> slots = new ArrayList<>();
        try (Connection con = connections.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(new TimeSlot(doctorId, TimeSlot.parse(rs.getString("slot")), rs.getInt("booked") == 1));
                }
            }
        } catch (SQLException e) {
            System.err.println("findAllSlots failed: " + e.getMessage());
        }
        return slots;
    }

    @Override
    public void addSlot(int doctorId, LocalDateTime slot) {
        String sql = "INSERT OR IGNORE INTO availability (d_id, slot) VALUES (?, ?)";
        try (Connection con = connections.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            stmt.setString(2, slot.format(TimeSlot.FORMAT));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("addSlot failed: " + e.getMessage());
        }
    }

    @Override
    public void removeSlot(int doctorId, LocalDateTime slot) {
        String sql = "DELETE FROM availability WHERE d_id = ? AND slot = ?";
        try (Connection con = connections.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            stmt.setString(2, slot.format(TimeSlot.FORMAT));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("removeSlot failed: " + e.getMessage());
        }
    }
}
