package org.example.Objects.UIObjects;

import javafx.scene.control.TextField;

public class StyledTextField extends TextField {
    public StyledTextField(String prompt) {
        setPromptText(prompt);
        setStyle(Theme.INPUT_IDLE);
        setMaxWidth(Double.MAX_VALUE);
        focusedProperty().addListener((obs, was, is) -> setStyle(is ? Theme.INPUT_FOCUS : Theme.INPUT_IDLE));
    }
}