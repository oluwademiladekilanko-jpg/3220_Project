package org.example.backend.persistence;

import org.example.Objects.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    /** Atomically inserts only if the doctor offers the slot AND it is not already taken. */
    boolean insertIfSlotFree(int patientId, int doctorId, LocalDateTime slot, String location);

    boolean deleteById(int appointmentId);

    Optional<Appointment> findById(int appointmentId);

    List<Appointment> findByPatient(int patientId);

    List<Appointment> findAll();
}
