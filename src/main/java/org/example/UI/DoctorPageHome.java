package org.example.UI;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.Objects.Appointment;
import org.example.Objects.TimeSlot;
import org.example.Objects.UIObjects.GlobalPageStandard;
import org.example.Objects.UIObjects.Theme;
import org.example.backend.Session;
import org.example.backend.service.AppContext;
import org.example.backend.service.AppointmentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Doctor workspace: today at a glance, the schedule of booked patients, and their own availability (read-only). */
public class DoctorPageHome extends GlobalPageStandard {
    private final AppointmentService service = AppContext.get().appointments();
    private final int doctorId = Session.id;

    public DoctorPageHome(Stage stage) {
        super(stage, "doctor");
        addNav("Today", this::today);
        addNav("Schedule", this::schedule);
        addNav("My availability", this::availability);
        openNav(0);
    }

    private VBox head(String title, String subtitle) {
        return new VBox(4, Theme.title(title), Theme.subtitle(subtitle));
    }

    private List<Appointment> mine() {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : service.allAppointments()) {
            if (a.getDoctor().getId() == doctorId) result.add(a);
        }
        return result;
    }

    private Node today() {
        LocalDateTime now = service.now();
        int todayCount = 0;
        int upcoming = 0;
        for (Appointment a : mine()) {
            if (a.getDate().toLocalDate().equals(now.toLocalDate())) todayCount++;
            if (a.getDate().isAfter(now)) upcoming++;
        }
        int open = service.openSlots(doctorId).size();

        HBox stats = new HBox(16,
                Theme.statCard("Today", String.valueOf(todayCount), todayCount == 1 ? "appointment" : "appointments"),
                Theme.statCard("Upcoming", String.valueOf(upcoming), "booked patients"),
                Theme.statCard("Open times", String.valueOf(open), "still available to book"));

        List<List<Node>> rows = new ArrayList<>();
        int shown = 0;
        for (Appointment a : mine()) {
            if (!a.getDate().isAfter(now) || shown >= 5) continue;
            rows.add(patientRow(a));
            shown++;
        }
        VBox page = new VBox(20, head("Your day", "A quick look at what is next."), stats,
                Theme.text("Next patients", 15, Theme.TEXT, true),
                Theme.table(new String[]{"When", "Patient", "Reason for visit"}, new double[]{190, 0, 260}, rows, "No upcoming appointments."));
        return page;
    }

    private List<Node> patientRow(Appointment a) {
        HBox who = new HBox(10, Theme.avatar(a.getPatient().getName(), 28), Theme.text(a.getPatient().getName(), 13, Theme.TEXT, true));
        who.setAlignment(Pos.CENTER_LEFT);
        return Arrays.<Node>asList(
                Theme.text(Theme.when(a.getDate()), 13, Theme.TEXT, true),
                who,
                Theme.text(a.getPatient().getIllness(), 13, Theme.TEXT_2, false));
    }

    private Node schedule() {
        LocalDateTime now = service.now();
        List<List<Node>> rows = new ArrayList<>();
        for (Appointment a : mine()) {
            boolean future = a.getDate().isAfter(now);
            rows.add(Arrays.<Node>asList(
                    Theme.text(Theme.when(a.getDate()), 13, Theme.TEXT, true),
                    new HBox(10, Theme.avatar(a.getPatient().getName(), 28), Theme.text(a.getPatient().getName(), 13, Theme.TEXT, true)),
                    Theme.text(a.getPatient().getIllness(), 13, Theme.TEXT_2, false),
                    Theme.text(a.getLocation(), 13, Theme.TEXT_2, false),
                    Theme.lozenge(future ? "Upcoming" : "Completed", future ? Theme.Tone.GREEN : Theme.Tone.NEUTRAL)));
        }
        return new VBox(20, head("Schedule", "Every appointment booked with you."),
                Theme.table(new String[]{"When", "Patient", "Reason for visit", "Location", "Status"},
                        new double[]{190, 0, 220, 90, 110}, rows, "No appointments have been booked with you yet."));
    }

    private Node availability() {
        List<List<Node>> rows = new ArrayList<>();
        for (TimeSlot s : service.allSlots(doctorId)) {
            rows.add(Arrays.<Node>asList(
                    Theme.text(Theme.when(s.getStart()), 13, Theme.TEXT, true),
                    Theme.lozenge(s.isBooked() ? "Booked" : "Open", s.isBooked() ? Theme.Tone.AMBER : Theme.Tone.GREEN)));
        }
        return new VBox(20, head("My availability", "Times the front desk has opened for you. Ask reception to change them."),
                Theme.table(new String[]{"Time slot", "Status"}, new double[]{0, 140}, rows, "No availability has been set yet."));
    }
}