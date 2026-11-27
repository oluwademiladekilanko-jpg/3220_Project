package org.example.backend.service;

import org.example.Objects.Appointment;
import org.example.Objects.Doctor;
import org.example.Objects.Role;
import org.example.Objects.TimeSlot;
import org.example.backend.persistence.AppointmentRepository;
import org.example.backend.persistence.AvailabilityRepository;
import org.example.backend.persistence.UserRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Application logic for booking, cancelling and managing availability. Depends only on abstractions. */
public class AppointmentService {
    public static final String DEFAULT_LOCATION = "Clinic";

    private final AppointmentRepository appointments;
    private final AvailabilityRepository availability;
    private final UserRepository users;
    private final Map<Role, CancellationPolicy> cancellationPolicies;
    private final Clock clock;

    public AppointmentService(AppointmentRepository appointments, AvailabilityRepository availability,
                              UserRepository users, Map<Role, CancellationPolicy> cancellationPolicies, Clock clock) {
        this.appointments = appointments;
        this.availability = availability;
        this.users = users;
        this.cancellationPolicies = cancellationPolicies;
        this.clock = clock;
    }

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    /** Fails if the slot is in the past, not offered, or already taken. */
    public boolean book(int patientId, int doctorId, LocalDateTime slot) {
        if (!slot.isAfter(now())) return false;
        return appointments.insertIfSlotFree(patientId, doctorId, slot, DEFAULT_LOCATION);
    }

    /** The actor's role decides which CancellationPolicy applies; roles without a policy (e.g. doctors) cannot cancel. */
    public boolean cancel(int appointmentId, SessionContext actor) {
        if (actor == null || actor.getRole() == null) return false;
        CancellationPolicy policy = cancellationPolicies.get(actor.getRole());
        if (policy == null) return false;
        Optional<Appointment> found = appointments.findById(appointmentId);
        return found.isPresent()
                && policy.permits(found.get(), actor, now())
                && appointments.deleteById(appointmentId);
    }

    public List<Appointment> appointmentsForPatient(int patientId) {
        return appointments.findByPatient(patientId);
    }

    public List<Appointment> allAppointments() {
        return appointments.findAll();
    }

    public List<String> doctorFields() {
        return users.findDoctorFields();
    }

    public List<Doctor> doctors(String field) {
        return users.findDoctors(field);
    }

    public List<TimeSlot> openSlots(int doctorId) {
        return availability.findOpenSlots(doctorId, now());
    }

    public List<TimeSlot> allSlots(int doctorId) {
        return availability.findAllSlots(doctorId);
    }

    /** Receptionist-only. Returns false when the actor is not allowed. */
    public boolean addSlot(SessionContext actor, int doctorId, LocalDateTime slot) {
        if (actor == null || !actor.hasRole(Role.RECEPTIONIST)) return false;
        availability.addSlot(doctorId, slot);
        return true;
    }

    /** Receptionist-only. Returns false when the actor is not allowed. */
    public boolean removeSlot(SessionContext actor, int doctorId, LocalDateTime slot) {
        if (actor == null || !actor.hasRole(Role.RECEPTIONIST)) return false;
        availability.removeSlot(doctorId, slot);
        return true;
    }
}
