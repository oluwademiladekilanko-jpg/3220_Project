package org.example.Objects.UIObjects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Single source of truth for the look of the app: colours, typography and small reusable components.
 * Pages never hard-code colours; they ask Theme. Change a value here and every screen follows.
 */
public final class Theme {
    private Theme() { }

    // Palette: near-black canvas with slightly lighter surfaces (Linear), indigo accent (Clerk), status lozenges (Atlassian)
    public static final String BG = "#0A0A0B";
    public static final String SURFACE = "#111113";
    public static final String SURFACE_2 = "#16161A";
    public static final String BORDER = "#26262B";
    public static final String TEXT = "#EDEDEF";
    public static final String TEXT_2 = "#A1A1AA";
    public static final String TEXT_3 = "#71717A";
    public static final String ACCENT = "#5B5BD6";
    public static final String ACCENT_HOVER = "#6B6BE3";
    public static final String ACCENT_TEXT = "#9B9BFA";
    public static final String ACCENT_SOFT = "#1B1B33";

    public static final String INPUT_IDLE = "-fx-background-color: #0F0F12; -fx-text-fill: " + TEXT + "; -fx-prompt-text-fill: " + TEXT_3
            + "; -fx-border-color: #2E2E35; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 9 12; -fx-font-size: 13px;";
    public static final String INPUT_FOCUS = "-fx-background-color: #0F0F12; -fx-text-fill: " + TEXT + "; -fx-prompt-text-fill: " + TEXT_3
            + "; -fx-border-color: " + ACCENT + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 9 12; -fx-font-size: 13px; "
            + "-fx-effect: dropshadow(gaussian, rgba(107,107,227,0.35), 6, 0, 0, 0);";

    public enum Tone { NEUTRAL, BLUE, GREEN, AMBER, RED }

    private static final String[] AVATARS = {"#5B5BD6", "#0C66E4", "#1F845A", "#C25100", "#AE2E24", "#6E5DC6", "#206A83"};
    private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("EEE, MMM d 'at' HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter SHORT = DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.ENGLISH);

    /** URL of the dark popup stylesheet (src/main/resources/dark.css), or null if the file is missing. */
    public static String stylesheet() {
        java.net.URL url = Theme.class.getResource("/dark.css");
        return url == null ? null : url.toExternalForm();
    }

    public static String rootStyle() {
        return "-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-background-color: " + BG + ";";
    }

    public static String fg(Tone t) {
        switch (t) {
            case BLUE: return "#7DB5FF";
            case GREEN: return "#4ADE9A";
            case AMBER: return "#F5C451";
            case RED: return "#FF7A72";
            default: return TEXT_2;
        }
    }

    public static String bg(Tone t) {
        switch (t) {
            case BLUE: return "#12233D";
            case GREEN: return "#0F2A20";
            case AMBER: return "#2E2410";
            case RED: return "#34151A";
            default: return "#1C1C20";
        }
    }

    // ---------- text ----------
    public static Label text(String s, int size, String color, boolean bold) {
        Label l = new Label(s);
        l.setStyle("-fx-font-size: " + size + "px; -fx-text-fill: " + color + "; -fx-font-weight: " + (bold ? "600" : "normal") + ";");
        return l;
    }

    public static Label title(String s) { return text(s, 24, TEXT, true); }

    public static Label subtitle(String s) { return text(s, 13, TEXT_2, false); }

    public static VBox labeled(String label, Node field) {
        return new VBox(6, text(label, 12, TEXT_2, true), field);
    }

    public static String when(LocalDateTime dt) { return dt.format(WHEN); }

    public static String shortDate(LocalDateTime dt) { return dt.format(SHORT); }

    // ---------- surfaces ----------
    public static VBox card() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: " + SURFACE + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;");
        box.setEffect(new DropShadow(10, 0, 2, Color.rgb(0, 0, 0, 0.45)));
        return box;
    }

    public static VBox statCard(String label, String value, String hint) {
        VBox box = card();
        box.setSpacing(4);
        box.getChildren().addAll(text(label.toUpperCase(), 11, TEXT_3, true), text(value, 22, TEXT, true), text(hint, 12, TEXT_2, false));
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    public static VBox emptyState(String message) {
        VBox box = new VBox(text(message, 13, TEXT_3, false));
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(32));
        return box;
    }

    // ---------- small components ----------
    public static Label lozenge(String text, Tone tone) {
        Label l = new Label(text.toUpperCase());
        l.setStyle("-fx-background-color: " + bg(tone) + "; -fx-text-fill: " + fg(tone) + "; -fx-background-radius: 4; "
                + "-fx-padding: 2 6; -fx-font-size: 11px; -fx-font-weight: bold;");
        return l;
    }

    public static String initials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        String s = parts[0].substring(0, 1);
        if (parts.length > 1) s += parts[1].substring(0, 1);
        return s.toUpperCase();
    }

    public static StackPane avatar(String name, double size) {
        String safe = name == null ? "?" : name;
        Circle circle = new Circle(size / 2);
        circle.setFill(Color.web(AVATARS[Math.abs(safe.hashCode() % AVATARS.length)]));
        Label l = new Label(initials(safe));
        l.setStyle("-fx-text-fill: white; -fx-font-size: " + (int) (size * 0.38) + "px; -fx-font-weight: bold;");
        return new StackPane(circle, l);
    }

    public static StackPane brandMark(double size, boolean onDark) {
        Label plus = new Label("+");
        plus.setStyle("-fx-font-size: " + (int) (size * 0.7) + "px; -fx-font-weight: bold; -fx-text-fill: " + (onDark ? ACCENT : "white") + ";");
        StackPane mark = new StackPane(plus);
        mark.setPrefSize(size, size);
        mark.setMinSize(size, size);
        mark.setMaxSize(size, size);
        mark.setStyle("-fx-background-radius: " + (size / 4) + "; -fx-background-color: " + (onDark ? "white" : ACCENT) + ";");
        return mark;
    }

    public static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        VBox.setVgrow(r, Priority.ALWAYS);
        return r;
    }

    public static void styleInput(Control c) {
        c.setStyle("-fx-background-color: #0F0F12; -fx-border-color: #2E2E35; -fx-border-radius: 8; -fx-background-radius: 8; "
                + "-fx-font-size: 13px; -fx-min-height: 36px;");
        c.setMaxWidth(Double.MAX_VALUE);
    }

    // ---------- inline messages (Atlassian-style) ----------
    public static Label message() {
        Label l = new Label();
        l.setWrapText(true);
        l.setMaxWidth(Double.MAX_VALUE);
        hide(l);
        return l;
    }

    public static void flash(Label l, String text, Tone tone) {
        l.setText(text);
        l.setStyle("-fx-background-color: " + bg(tone) + "; -fx-text-fill: " + fg(tone)
                + "; -fx-background-radius: 8; -fx-padding: 10 12; -fx-font-size: 12px;");
        l.setVisible(true);
        l.setManaged(true);
    }

    public static void hide(Label l) {
        l.setVisible(false);
        l.setManaged(false);
    }

    // ---------- data table: one component used by every list in the app ----------
    /** A width of 0 or less means "this column takes the leftover space". */
    public static VBox table(String[] headers, double[] widths, List<List<Node>> rows, String emptyText) {
        VBox box = new VBox();
        box.setStyle("-fx-background-color: " + SURFACE + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;");
        box.setEffect(new DropShadow(10, 0, 2, Color.rgb(0, 0, 0, 0.45)));

        HBox head = new HBox(12);
        head.setPadding(new Insets(10, 16, 10, 16));
        head.setStyle("-fx-background-color: #0E0E10; -fx-background-radius: 12 12 0 0; -fx-border-color: transparent transparent "
                + BORDER + " transparent; -fx-border-width: 0 0 1 0;");
        for (int i = 0; i < headers.length; i++) {
            head.getChildren().add(cell(text(headers[i].toUpperCase(), 11, TEXT_3, true), widths[i]));
        }
        box.getChildren().add(head);

        if (rows.isEmpty()) {
            box.getChildren().add(emptyState(emptyText));
        }
        final String rowBase = "-fx-border-color: transparent transparent " + BORDER + " transparent; -fx-border-width: 0 0 1 0; -fx-background-color: ";
        for (List<Node> cells : rows) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle(rowBase + "" + SURFACE + ";");
            row.setOnMouseEntered(e -> row.setStyle(rowBase + "#17171B;"));
            row.setOnMouseExited(e -> row.setStyle(rowBase + "" + SURFACE + ";"));
            for (int i = 0; i < cells.size(); i++) {
                row.getChildren().add(cell(cells.get(i), widths[i]));
            }
            box.getChildren().add(row);
        }
        return box;
    }

    private static HBox cell(Node content, double width) {
        HBox h = new HBox(content);
        h.setAlignment(Pos.CENTER_LEFT);
        if (width > 0) {
            h.setPrefWidth(width);
            h.setMinWidth(width);
            h.setMaxWidth(width);
        } else {
            h.setMinWidth(120);
            HBox.setHgrow(h, Priority.ALWAYS);
        }
        return h;
    }
}
