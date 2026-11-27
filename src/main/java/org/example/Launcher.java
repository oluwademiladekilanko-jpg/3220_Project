package org.example;

import javafx.application.Application;

/** Entry point that is not itself an Application subclass, so the JavaFX runtime starts cleanly from Maven or an IDE. */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}