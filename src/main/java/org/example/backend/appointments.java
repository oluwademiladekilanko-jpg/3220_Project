package org.example.backend;

import org.example.Objects.Appointment;
import org.example.Objects.Doctor;
import org.example.Objects.Role;
import org.example.Objects.TimeSlot;
import org.example.backend.service.AppContext;
import org.example.backend.service.AppointmentService;
import org.example.backend.service.SessionContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Transitional facade: keeps the String[] row API the UI pages already use, but holds no logic or SQL.
 * Rules live in AppointmentService; queries live in the repositories.
 */
public class appointments {
    private static final DateTimeFormatter FLEXIBLE_TIME = DateTimeFormatter.ofPattern("H:mm");

    private static AppointmentService service() {
        return AppContext.get().appointments();
    }

    public static String now() {
        return service().now().format(TimeSlot.FORMAT);
    }

    public static boolean book(int pid, int did, String slot) {
        try {
            return service().book(pid, did, TimeSlot.parse(slot));
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /** A patient cancelling their own future appointment. */
    public static boolean cancelOwn(int aid, int pid) {
        return service().cancel(aid, new SessionContext(Role.PATIENT, pid));
    }

    /** Only succeeds when the logged-in user is a receptionist. */
    public static boolean cancelAny(int aid) {
        return service().cancel(aid, Session.current());
    }

    public static List<String[]> forPatient(int pid) {
        List<String[]> rows = new ArrayList<>();
        for (Appointment a : service().appointmentsForPatient(pid)) {
            rows.add(new String[]{
                    String.valueOf(a.getId()),
                    a.getDate().format(TimeSlot.FORMAT),
                    a.getDoc().getName(),
                    a.getDoc().getField(),
                    a.getLocation()});
        }
        return rows;
    }

    public static List<String[]> fields() {
        List<String[]> rows = new ArrayList<>();
        for (String field : service().doctorFields()) {
            rows.add(new String[]{field});
        }
        return rows;
    }

    public static List<String[]> doctors(String field) {
        List<String[]> rows = new ArrayList<>();
        for (Doctor d : service().doctors(field)) {
            rows.add(new String[]{String.valueOf(d.getId()), d.getName(), d.getField()});
        }
        return rows;
    }

    public static List<String[]> openSlots(int doctorId) {
        List<String[]> rows = new ArrayList<>();
        for (TimeSlot s : service().openSlots(doctorId)) {
            rows.add(new String[]{s.format()});
        }
        return rows;
    }

    public static List<String[]> allSlots(int doctorId) {
        List<String[]> rows = new ArrayList<>();
        for (TimeSlot s : service().allSlots(doctorId)) {
            rows.add(new String[]{s.format(), s.isBooked() ? "1" : "0"});
        }
        return rows;
    }

    public static List<String[]> all() {
        List<String[]> rows = new ArrayList<>();
        for (Appointment a : service().allAppointments()) {
            rows.add(new String[]{
                    String.valueOf(a.getId()),
                    a.getDate().format(TimeSlot.FORMAT),
                    a.getPatient().getName(),
                    a.getDoc().getName()});
        }
        return rows;
    }

    // receptionist specific (enforced in AppointmentService)
    public static void addSlot(int doctorId, LocalDate date, String time) {
        try {
            LocalDateTime slot = LocalDateTime.of(date, LocalTime.parse(time.trim(), FLEXIBLE_TIME));
            service().addSlot(Session.current(), doctorId, slot);
        } catch (DateTimeParseException e) {
            System.err.println("addSlot: bad time '" + time + "'");
        }
    }

    public static void removeSlot(int doctorId, String slot) {
        try {
            service().removeSlot(Session.current(), doctorId, TimeSlot.parse(slot));
        } catch (DateTimeParseException e) {
            System.err.println("removeSlot: bad slot '" + slot + "'");
        }
    }
}
