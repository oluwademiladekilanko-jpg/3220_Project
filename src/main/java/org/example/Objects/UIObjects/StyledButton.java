package org.example.Objects.UIObjects;

import javafx.scene.Cursor;
import javafx.scene.control.Button;

public class StyledButton extends Button {
    public enum Kind { PRIMARY, SECONDARY, GHOST, DANGER }

    private final Kind kind;

    /** Kept for compatibility with older pages: a plain StyledButton is a primary button. */
    public StyledButton(String text) {
        this(text, Kind.PRIMARY);
    }

    public StyledButton(String text, Kind kind) {
        super(text);
        this.kind = kind;
        setCursor(Cursor.HAND);
        apply(false);
        setOnMouseEntered(e -> apply(true));
        setOnMouseExited(e -> apply(false));
    }

    private void apply(boolean hover) {
        String base = "-fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 8 16; -fx-font-size: 13px; -fx-font-weight: 600; ";
        switch (kind) {
            case SECONDARY:
                setStyle(base + "-fx-background-color: " + (hover ? "#1C1C20" : "#141417") + "; -fx-border-color: #2E2E35; -fx-text-fill: " + Theme.TEXT + ";");
                break;
            case GHOST:
                setStyle(base + "-fx-background-color: " + (hover ? "#1C1C20" : "transparent") + "; -fx-text-fill: " + Theme.TEXT_2 + ";");
                break;
            case DANGER:
                setStyle(base + "-fx-background-color: " + (hover ? "#4A1D22" : "#34151A") + "; -fx-text-fill: #FF7A72;");
                break;
            default:
                setStyle(base + "-fx-background-color: " + (hover ? Theme.ACCENT_HOVER : Theme.ACCENT) + "; -fx-text-fill: white;");
        }
    }
}
