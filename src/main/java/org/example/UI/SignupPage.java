package org.example.UI;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.Objects.Role;
import org.example.Objects.UIObjects.StyledButton;
import org.example.Objects.UIObjects.StyledPasswordField;
import org.example.Objects.UIObjects.StyledTextField;
import org.example.Objects.UIObjects.Theme;
import org.example.backend.service.AppContext;
import org.example.backend.service.SignupService;

/** Account creation: a role switcher on top, with only the fields that role needs below it. */
public class SignupPage extends StackPane {
    private final Stage stage;
    private final SignupService signup = AppContext.get().signup();
    private Role selected = Role.PATIENT;

    private final StyledTextField name = new StyledTextField("Full name");
    private final StyledTextField email = new StyledTextField("you@example.com");
    private final StyledPasswordField password = new StyledPasswordField("Choose a password");
    private final StyledTextField phone = new StyledTextField("Phone number");
    private final StyledTextField illness = new StyledTextField("Reason for visiting");
    private final StyledTextField symptoms = new StyledTextField("Symptoms (optional)");
    private final StyledTextField specialty = new StyledTextField("e.g. Family medicine");
    private final StyledTextField emergency = new StyledTextField("Emergency phone number");
    private final VBox extraFields = new VBox(12);
    private final Label message = Theme.message();
    private final StyledButton submit = new StyledButton("Create account");

    public SignupPage(Stage stage) {
        this.stage = stage;
        setStyle("-fx-background-color: linear-gradient(to bottom right, #101018, #0A0A0B 60%); " + Theme.rootStyle().replace("-fx-background-color: " + Theme.BG + ";", ""));

        submit.setMaxWidth(Double.MAX_VALUE);
        submit.setOnAction(e -> submit());

        Hyperlink back = new Hyperlink("Sign in");
        back.setStyle("-fx-text-fill: " + Theme.ACCENT_TEXT + "; -fx-font-weight: 600; -fx-padding: 0; -fx-border-color: transparent;");
        back.setOnAction(e -> stage.getScene().setRoot(new LoginPage(stage)));
        HBox footer = new HBox(6, Theme.text("Already have an account?", 13, Theme.TEXT_2, false), back);
        footer.setAlignment(Pos.CENTER);

        VBox card = Theme.card();
        card.setSpacing(14);
        card.setPadding(new Insets(32));
        card.setMaxWidth(460);
        card.getChildren().addAll(
                Theme.brandMark(32, false),
                new VBox(4, Theme.text("Create your account", 22, Theme.TEXT, true), Theme.subtitle("Choose who you are, then fill in your details")),
                segmented(),
                Theme.labeled("Full name", name),
                Theme.labeled("Email address", email),
                Theme.labeled("Password", password),
                Theme.labeled("Phone", phone),
                extraFields, message, submit, footer);
        buildExtras();

        VBox page = new VBox(card);
        page.setAlignment(Pos.TOP_CENTER);
        page.setPadding(new Insets(32));
        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        getChildren().add(scroll);
    }

    private HBox segmented() {
        ToggleGroup group = new ToggleGroup();
        HBox bar = new HBox(4);
        bar.setPadding(new Insets(4));
        bar.setStyle("-fx-background-color: #1C1C20; -fx-background-radius: 10;");
        for (Role r : Role.values()) {
            String label = r == Role.RECEPTIONIST ? "Reception" : r.getTableName().substring(0, 1).toUpperCase() + r.getTableName().substring(1);
            ToggleButton b = new ToggleButton(label);
            b.setToggleGroup(group);
            b.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(b, Priority.ALWAYS);
            b.setSelected(r == selected);
            styleSegment(b);
            b.selectedProperty().addListener((o, was, is) -> {
                styleSegment(b);
                if (is) {
                    selected = r;
                    buildExtras();
                }
            });
            bar.getChildren().add(b);
        }
        group.selectedToggleProperty().addListener((o, old, now) -> {
            if (now == null && old != null) old.setSelected(true);
        });
        return bar;
    }

    private void styleSegment(ToggleButton b) {
        b.setStyle("-fx-background-radius: 8; -fx-padding: 7 12; -fx-font-size: 13px; -fx-font-weight: 600; -fx-cursor: hand; "
                + (b.isSelected()
                ? "-fx-background-color: #2A2A33; -fx-text-fill: " + Theme.TEXT + ";"
                : "-fx-background-color: transparent; -fx-text-fill: " + Theme.TEXT_2 + ";"));
    }

    private void buildExtras() {
        extraFields.getChildren().clear();
        switch (selected) {
            case DOCTOR:
                extraFields.getChildren().addAll(Theme.labeled("Specialty", specialty), Theme.labeled("Emergency phone", emergency));
                break;
            case PATIENT:
                extraFields.getChildren().addAll(Theme.labeled("Reason for visit", illness), Theme.labeled("Symptoms", symptoms));
                break;
            default:
                break;
        }
        Theme.hide(message);
    }

    private void submit() {
        String result;
        switch (selected) {
            case DOCTOR:
                result = signup.signupDoctor(name.getText(), email.getText(), password.getText(), phone.getText(), specialty.getText(), emergency.getText());
                break;
            case RECEPTIONIST:
                result = signup.signupReceptionist(name.getText(), email.getText(), password.getText(), phone.getText());
                break;
            default:
                result = signup.signupPatient(name.getText(), email.getText(), password.getText(), phone.getText(), illness.getText(), symptoms.getText());
        }
        if ("success".equals(result)) {
            Theme.flash(message, "Account created. Taking you to sign in...", Theme.Tone.GREEN);
            submit.setDisable(true);
            PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
            pause.setOnFinished(e -> stage.getScene().setRoot(new LoginPage(stage)));
            pause.play();
        } else {
            Theme.flash(message, result.substring(0, 1).toUpperCase() + result.substring(1) + ".", Theme.Tone.RED);
        }
    }
}
