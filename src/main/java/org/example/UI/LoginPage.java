package org.example.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.Objects.UIObjects.StyledButton;
import org.example.Objects.UIObjects.StyledPasswordField;
import org.example.Objects.UIObjects.StyledTextField;
import org.example.Objects.UIObjects.Theme;
import org.example.backend.Session;
import org.example.backend.service.AppContext;
import org.example.backend.service.SessionContext;

import java.util.Optional;

/** Split-screen sign in: brand panel on the left, a centred form card on the right. */
public class LoginPage extends HBox {
    private final Stage stage;
    private final StyledTextField email = new StyledTextField("you@example.com");
    private final StyledPasswordField password = new StyledPasswordField("Enter your password");
    private final Label message = Theme.message();

    public LoginPage(Stage stage) {
        this.stage = stage;
        setStyle(Theme.rootStyle());
        Region form = formPanel();
        HBox.setHgrow(form, Priority.ALWAYS);
        getChildren().addAll(brandPanel(), form);
    }

    private VBox brandPanel() {
        VBox panel = new VBox(18);
        panel.setPadding(new Insets(48));
        panel.setPrefWidth(460);
        panel.setMinWidth(380);
        panel.setStyle("-fx-background-color: linear-gradient(to bottom right, #1B1B3A, #0A0A0B);");

        HBox brand = new HBox(10, Theme.brandMark(30, true), whiteText("Clinic Portal", 18, "bold", "white"));
        brand.setAlignment(Pos.CENTER_LEFT);

        Label headline = whiteText("Appointments without the phone tag.", 32, "bold", "white");
        headline.setWrapText(true);
        Label sub = whiteText("One place for patients, doctors and the front desk.", 14, "normal", "rgba(255,255,255,0.8)");
        sub.setWrapText(true);

        VBox features = new VBox(10,
                whiteText("-  Book an open time in a few clicks", 13, "normal", "rgba(255,255,255,0.9)"),
                whiteText("-  Doctors see their day at a glance", 13, "normal", "rgba(255,255,255,0.9)"),
                whiteText("-  Reception manages availability and cancellations", 13, "normal", "rgba(255,255,255,0.9)"));

        panel.getChildren().addAll(brand, Theme.spacer(), headline, sub, features, Theme.spacer(),
                whiteText("COMP 3220 Clinic Appointment Booking System", 11, "normal", "rgba(255,255,255,0.6)"));
        return panel;
    }

    private StackPane formPanel() {
        StackPane panel = new StackPane();
        panel.setStyle("-fx-background-color: linear-gradient(to bottom right, #101018, #0A0A0B 60%);");

        email.setOnAction(e -> password.requestFocus());
        password.setOnAction(e -> attempt());

        StyledButton submit = new StyledButton("Continue");
        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setOnAction(e -> attempt());

        Label hint = Theme.text("Doctors sign in with @doctor.ca and reception with @clinic.ca. Everyone else is a patient.",
                12, Theme.fg(Theme.Tone.BLUE), false);
        hint.setWrapText(true);
        hint.setStyle(hint.getStyle() + " -fx-background-color: " + Theme.bg(Theme.Tone.BLUE) + "; -fx-background-radius: 8; -fx-padding: 10 12;");
        hint.setMaxWidth(Double.MAX_VALUE);

        Hyperlink create = new Hyperlink("Create an account");
        create.setStyle("-fx-text-fill: " + Theme.ACCENT_TEXT + "; -fx-font-weight: 600; -fx-padding: 0; -fx-border-color: transparent;");
        create.setOnAction(e -> stage.getScene().setRoot(new SignupPage(stage)));
        HBox footer = new HBox(6, Theme.text("New to the clinic?", 13, Theme.TEXT_2, false), create);
        footer.setAlignment(Pos.CENTER);

        VBox card = Theme.card();
        card.setSpacing(16);
        card.setPadding(new Insets(32));
        card.setPrefWidth(400);
        card.getChildren().addAll(
                Theme.brandMark(32, false),
                new VBox(4, Theme.text("Welcome back", 22, Theme.TEXT, true), Theme.subtitle("Sign in to your clinic account")),
                Theme.labeled("Email address", email),
                Theme.labeled("Password", password),
                message, submit, hint, footer);

        VBox wrap = new VBox(14, card, Theme.text("Secured by Clinic Portal", 11, Theme.TEXT_3, false));
        wrap.setAlignment(Pos.CENTER);
        wrap.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        panel.getChildren().add(wrap);
        return panel;
    }

    private void attempt() {
        String mail = email.getText();
        String pw = password.getText();
        if (mail == null || mail.trim().isEmpty() || pw == null || pw.isEmpty()) {
            Theme.flash(message, "Enter your email and password.", Theme.Tone.RED);
            return;
        }
        Optional<SessionContext> result = AppContext.get().auth().login(mail, pw);
        if (!result.isPresent()) {
            Theme.flash(message, "Incorrect email or password.", Theme.Tone.RED);
            password.clear();
            return;
        }
        SessionContext ctx = result.get();
        Session.id = ctx.getUserId();
        Session.role = ctx.getRole().getTableName();

        Parent next;
        switch (ctx.getRole()) {
            case DOCTOR: next = new DoctorPageHome(stage); break;
            case RECEPTIONIST: next = new ReceptionistPageHome(stage); break;
            default: next = new PatientPageHome(stage);
        }
        stage.getScene().setRoot(next);
    }

    private static Label whiteText(String s, int size, String weight, String color) {
        Label l = new Label(s);
        l.setStyle("-fx-font-size: " + size + "px; -fx-font-weight: " + weight + "; -fx-text-fill: " + color + ";");
        return l;
    }
}
