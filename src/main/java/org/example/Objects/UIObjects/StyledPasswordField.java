package org.example.Objects.UIObjects;

import javafx.scene.control.PasswordField;

public class StyledPasswordField extends PasswordField {
    public StyledPasswordField(String prompt) {
        setPromptText(prompt);
        setStyle(Theme.INPUT_IDLE);
        setMaxWidth(Double.MAX_VALUE);
        focusedProperty().addListener((obs, was, is) -> setStyle(is ? Theme.INPUT_FOCUS : Theme.INPUT_IDLE));
    }
}