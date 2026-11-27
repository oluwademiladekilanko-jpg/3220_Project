package org.example.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.Objects.Appointment;
import org.example.Objects.Doctor;
import org.example.Objects.TimeSlot;
import org.example.Objects.UIObjects.GlobalPageStandard;
import org.example.Objects.UIObjects.StyledButton;
import org.example.Objects.UIObjects.Theme;
import org.example.backend.Session;
import org.example.backend.service.AppContext;
import org.example.backend.service.AppointmentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Patient workspace: an overview, a guided booking flow, and the list of their own appointments. */
public class PatientPageHome extends GlobalPageStandard {
    private static final String ALL = "All specialties";

    private final AppointmentService service = AppContext.get().appointments();
    private final int patientId = Session.id;

    private String fieldFilter;
    private Doctor selectedDoctor;
    private String notice;
    private Theme.Tone noticeTone = Theme.Tone.GREEN;

    public PatientPageHome(Stage stage) {
        super(stage, "patient");
        addNav("Overview", this::overview);
        addNav("Book appointment", this::booking);
        addNav("My appointments", this::myAppointments);
        openNav(0);
    }

    private VBox head(String title, String subtitle) {
        return new VBox(4, Theme.title(title), Theme.subtitle(subtitle));
    }

    // ---------- Overview ----------
    private Node overview() {
        LocalDateTime now = service.now();
        List<Appointment> mine = service.appointmentsForPatient(patientId);
        Appointment next = null;
        int upcoming = 0;
        for (Appointment a : mine) {
            if (a.getDate().isAfter(now)) {
                upcoming++;
                if (next == null) next = a; // list is ordered by date
            }
        }
        String nextValue = next == null ? "Nothing booked" : Theme.shortDate(next.getDate());
        String nextHint = next == null ? "Book your first visit" : "with " + next.getDoc().getName();

        HBox stats = new HBox(16,
                Theme.statCard("Upcoming", String.valueOf(upcoming), upcoming == 1 ? "appointment" : "appointments"),
                Theme.statCard("Next visit", nextValue, nextHint),
                Theme.statCard("Total booked", String.valueOf(mine.size()), "all time"));

        StyledButton book = new StyledButton("Book an appointment");
        book.setOnAction(e -> openNav(1));
        VBox cta = Theme.card();
        cta.getChildren().addAll(Theme.text("Need to see a doctor?", 15, Theme.TEXT, true),
                Theme.subtitle("Choose a specialty, pick a doctor and take any open time."), book);

        return new VBox(20, head("Welcome back", "Here is what is coming up for you."), stats, cta);
    }

    // ---------- Booking ----------
    private Node booking() {
        VBox page = new VBox(20, head("Book an appointment", "Pick a specialty, choose a doctor, then select an open time."));

        Label message = Theme.message();
        if (notice != null) {
            Theme.flash(message, notice, noticeTone);
            notice = null;
        }
        page.getChildren().add(message);

        ComboBox<String> specialty = new ComboBox<>();
        specialty.getItems().add(ALL);
        specialty.getItems().addAll(service.doctorFields());
        specialty.setValue(fieldFilter == null ? ALL : fieldFilter);
        Theme.styleInput(specialty);
        specialty.setPrefWidth(260);
        specialty.setOnAction(e -> {
            fieldFilter = ALL.equals(specialty.getValue()) ? null : specialty.getValue();
            selectedDoctor = null;
            refresh();
        });
        page.getChildren().add(Theme.labeled("Specialty", specialty));

        List<List<Node>> rows = new ArrayList<>();
        for (Doctor d : service.doctors(fieldFilter)) {
            HBox who = new HBox(10, Theme.avatar(d.getName(), 30), Theme.text(d.getName(), 13, Theme.TEXT, true));
            who.setAlignment(Pos.CENTER_LEFT);
            boolean chosen = selectedDoctor != null && selectedDoctor.getId() == d.getId();
            StyledButton choose = new StyledButton(chosen ? "Selected" : "See times",
                    chosen ? StyledButton.Kind.PRIMARY : StyledButton.Kind.SECONDARY);
            choose.setOnAction(e -> {
                selectedDoctor = d;
                refresh();
            });
            rows.add(Arrays.<Node>asList(who, Theme.lozenge(d.getField(), Theme.Tone.BLUE), choose));
        }
        page.getChildren().add(Theme.table(new String[]{"Doctor", "Specialty", ""}, new double[]{0, 200, 120}, rows,
                "No doctors are registered yet."));

        if (selectedDoctor != null) {
            page.getChildren().add(slotsCard(selectedDoctor));
        }
        return page;
    }

    private Node slotsCard(Doctor doctor) {
        VBox card = Theme.card();
        card.getChildren().add(Theme.text("Open times with " + doctor.getName(), 15, Theme.TEXT, true));
        List<TimeSlot> slots = service.openSlots(doctor.getId());
        if (slots.isEmpty()) {
            card.getChildren().add(Theme.emptyState("No open times right now. Please check back soon."));
            return card;
        }
        FlowPane flow = new FlowPane(10, 10);
        flow.setPadding(new Insets(4, 0, 0, 0));
        for (TimeSlot s : slots) {
            StyledButton chip = new StyledButton(Theme.shortDate(s.getStart()), StyledButton.Kind.SECONDARY);
            chip.setOnAction(e -> {
                if (!confirm("Book " + Theme.when(s.getStart()) + " with " + doctor.getName() + "?")) return;
                if (service.book(patientId, doctor.getId(), s.getStart())) {
                    notice = "Booked for " + Theme.when(s.getStart()) + " with " + doctor.getName() + ".";
                    noticeTone = Theme.Tone.GREEN;
                    selectedDoctor = null;
                } else {
                    notice = "That time was just taken. Please choose another.";
                    noticeTone = Theme.Tone.RED;
                }
                refresh();
            });
            flow.getChildren().add(chip);
        }
        card.getChildren().add(flow);
        return card;
    }

    // ---------- My appointments ----------
    private Node myAppointments() {
        VBox page = new VBox(20, head("My appointments", "Your upcoming and past visits."));
        Label message = Theme.message();
        if (notice != null) {
            Theme.flash(message, notice, noticeTone);
            notice = null;
        }
        page.getChildren().add(message);

        LocalDateTime now = service.now();
        List<List<Node>> rows = new ArrayList<>();
        for (Appointment a : service.appointmentsForPatient(patientId)) {
            boolean future = a.getDate().isAfter(now);
            Node action;
            if (future) {
                StyledButton cancel = new StyledButton("Cancel", StyledButton.Kind.DANGER);
                cancel.setOnAction(e -> {
                    if (!confirm("Cancel your appointment on " + Theme.when(a.getDate()) + "?")) return;
                    boolean ok = service.cancel(a.getId(), Session.current());
                    notice = ok ? "Appointment cancelled." : "That appointment could not be cancelled.";
                    noticeTone = ok ? Theme.Tone.GREEN : Theme.Tone.RED;
                    refresh();
                });
                action = cancel;
            } else {
                action = new Label("");
            }
            rows.add(Arrays.<Node>asList(
                    Theme.text(Theme.when(a.getDate()), 13, Theme.TEXT, true),
                    Theme.text(a.getDoc().getName(), 13, Theme.TEXT, false),
                    Theme.text(a.getDoc().getField(), 13, Theme.TEXT_2, false),
                    Theme.text(a.getLocation(), 13, Theme.TEXT_2, false),
                    Theme.lozenge(future ? "Upcoming" : "Completed", future ? Theme.Tone.GREEN : Theme.Tone.NEUTRAL),
                    action));
        }
        page.getChildren().add(Theme.table(
                new String[]{"When", "Doctor", "Specialty", "Location", "Status", ""},
                new double[]{190, 0, 150, 90, 110, 90}, rows, "You have no appointments yet."));
        return page;
    }
}