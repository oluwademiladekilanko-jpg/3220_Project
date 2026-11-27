package org.example.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.Objects.Appointment;
import org.example.Objects.Doctor;
import org.example.Objects.TimeSlot;
import org.example.Objects.UIObjects.GlobalPageStandard;
import org.example.Objects.UIObjects.StyledButton;
import org.example.Objects.UIObjects.StyledTextField;
import org.example.Objects.UIObjects.Theme;
import org.example.backend.Session;
import org.example.backend.service.AppContext;
import org.example.backend.service.AppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Reception workspace: clinic overview, every appointment (with cancellation), and per-doctor availability. */
public class ReceptionistPageHome extends GlobalPageStandard {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("H:mm");

    private final AppointmentService service = AppContext.get().appointments();

    private Doctor selectedDoctor;
    private String notice;
    private Theme.Tone noticeTone = Theme.Tone.GREEN;

    public ReceptionistPageHome(Stage stage) {
        super(stage, "receptionist");
        addNav("Overview", this::overview);
        addNav("Appointments", this::appointments);
        addNav("Availability", this::availability);
        openNav(0);
    }

    private VBox head(String title, String subtitle) {
        return new VBox(4, Theme.title(title), Theme.subtitle(subtitle));
    }

    private Label banner() {
        Label message = Theme.message();
        if (notice != null) {
            Theme.flash(message, notice, noticeTone);
            notice = null;
        }
        return message;
    }

    // ---------- Overview ----------
    private Node overview() {
        LocalDateTime now = service.now();
        List<Appointment> all = service.allAppointments();
        int today = 0;
        int upcoming = 0;
        for (Appointment a : all) {
            if (a.getDate().toLocalDate().equals(now.toLocalDate())) today++;
            if (a.getDate().isAfter(now)) upcoming++;
        }
        HBox stats = new HBox(16,
                Theme.statCard("Today", String.valueOf(today), "appointments"),
                Theme.statCard("Upcoming", String.valueOf(upcoming), "across the clinic"),
                Theme.statCard("Doctors", String.valueOf(service.doctors(null).size()), "on the roster"));

        StyledButton manage = new StyledButton("Manage availability");
        manage.setOnAction(e -> openNav(2));
        VBox cta = Theme.card();
        cta.getChildren().addAll(Theme.text("Keep the schedule full", 15, Theme.TEXT, true),
                Theme.subtitle("Open time slots for a doctor so patients can book them."), manage);
        return new VBox(20, head("Front desk", "The clinic at a glance."), stats, cta);
    }

    // ---------- Appointments ----------
    private Node appointments() {
        LocalDateTime now = service.now();
        List<List<Node>> rows = new ArrayList<>();
        for (Appointment a : service.allAppointments()) {
            boolean future = a.getDate().isAfter(now);
            StyledButton cancel = new StyledButton("Cancel", StyledButton.Kind.DANGER);
            cancel.setOnAction(e -> {
                if (!confirm("Cancel " + a.getPatient().getName() + "'s appointment on " + Theme.when(a.getDate()) + "?")) return;
                boolean ok = service.cancel(a.getId(), Session.current());
                notice = ok ? "Appointment cancelled." : "That appointment could not be cancelled.";
                noticeTone = ok ? Theme.Tone.GREEN : Theme.Tone.RED;
                refresh();
            });
            HBox who = new HBox(10, Theme.avatar(a.getPatient().getName(), 28), Theme.text(a.getPatient().getName(), 13, Theme.TEXT, true));
            who.setAlignment(Pos.CENTER_LEFT);
            rows.add(Arrays.<Node>asList(
                    Theme.text(Theme.when(a.getDate()), 13, Theme.TEXT, true),
                    who,
                    Theme.text(a.getDoc().getName(), 13, Theme.TEXT, false),
                    Theme.lozenge(future ? "Upcoming" : "Completed", future ? Theme.Tone.GREEN : Theme.Tone.NEUTRAL),
                    future ? (Node) cancel : new Label("")));
        }
        return new VBox(20, head("Appointments", "Every booking in the clinic."), banner(),
                Theme.table(new String[]{"When", "Patient", "Doctor", "Status", ""},
                        new double[]{190, 0, 180, 110, 90}, rows, "No appointments have been booked yet."));
    }

    // ---------- Availability ----------
    private Node availability() {
        VBox page = new VBox(20, head("Availability", "Open or remove time slots for a doctor."), banner());

        ComboBox<Doctor> picker = new ComboBox<>();
        picker.getItems().addAll(service.doctors(null));
        picker.setConverter(new StringConverter<Doctor>() {
            @Override
            public String toString(Doctor d) {
                return d == null ? "" : d.getName() + "  -  " + d.getField();
            }

            @Override
            public Doctor fromString(String s) {
                return null;
            }
        });
        if (selectedDoctor != null) {
            for (Doctor d : picker.getItems()) {
                if (d.getId() == selectedDoctor.getId()) picker.setValue(d);
            }
        }
        Theme.styleInput(picker);
        picker.setPrefWidth(320);
        picker.setPromptText("Choose a doctor");
        picker.setOnAction(e -> {
            selectedDoctor = picker.getValue();
            refresh();
        });
        page.getChildren().add(Theme.labeled("Doctor", picker));

        if (selectedDoctor == null) {
            VBox hint = Theme.card();
            hint.getChildren().add(Theme.emptyState("Choose a doctor to see and edit their time slots."));
            page.getChildren().add(hint);
            return page;
        }

        final int doctorId = selectedDoctor.getId();
        DatePicker date = new DatePicker();
        date.setPromptText("Date");
        Theme.styleInput(date);
        StyledTextField time = new StyledTextField("Time, e.g. 09:30");
        time.setPrefWidth(150);
        StyledButton add = new StyledButton("Add slot");
        Label addMessage = Theme.message();
        add.setOnAction(e -> {
            LocalDate d = date.getValue();
            if (d == null || time.getText() == null || time.getText().trim().isEmpty()) {
                Theme.flash(addMessage, "Choose a date and enter a time.", Theme.Tone.RED);
                return;
            }
            try {
                LocalDateTime slot = LocalDateTime.of(d, LocalTime.parse(time.getText().trim(), CLOCK));
                if (!slot.isAfter(service.now())) {
                    Theme.flash(addMessage, "Pick a time in the future.", Theme.Tone.RED);
                    return;
                }
                boolean ok = service.addSlot(Session.current(), doctorId, slot);
                notice = ok ? "Slot added for " + Theme.when(slot) + "." : "You are not allowed to change availability.";
                noticeTone = ok ? Theme.Tone.GREEN : Theme.Tone.RED;
                refresh();
            } catch (DateTimeParseException ex) {
                Theme.flash(addMessage, "Enter the time as hours and minutes, like 09:30.", Theme.Tone.RED);
            }
        });
        HBox form = new HBox(12, Theme.labeled("Date", date), Theme.labeled("Time", time), add);
        form.setAlignment(Pos.BOTTOM_LEFT);
        VBox formCard = Theme.card();
        formCard.setPadding(new Insets(20));
        formCard.getChildren().addAll(Theme.text("Add a time slot", 15, Theme.TEXT, true), form, addMessage);
        page.getChildren().add(formCard);

        List<List<Node>> rows = new ArrayList<>();
        for (TimeSlot s : service.allSlots(doctorId)) {
            Node action = new Label("");
            if (!s.isBooked()) {
                StyledButton remove = new StyledButton("Remove", StyledButton.Kind.DANGER);
                remove.setOnAction(e -> {
                    if (!confirm("Remove the slot on " + Theme.when(s.getStart()) + "?")) return;
                    boolean ok = service.removeSlot(Session.current(), doctorId, s.getStart());
                    notice = ok ? "Slot removed." : "You are not allowed to change availability.";
                    noticeTone = ok ? Theme.Tone.GREEN : Theme.Tone.RED;
                    refresh();
                });
                action = remove;
            }
            rows.add(Arrays.<Node>asList(
                    Theme.text(Theme.when(s.getStart()), 13, Theme.TEXT, true),
                    Theme.lozenge(s.isBooked() ? "Booked" : "Open", s.isBooked() ? Theme.Tone.AMBER : Theme.Tone.GREEN),
                    action));
        }
        page.getChildren().add(Theme.table(new String[]{"Time slot", "Status", ""}, new double[]{0, 120, 100}, rows,
                "This doctor has no time slots yet."));
        return page;
    }
}