package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.Objects.UIObjects.Theme;
import org.example.UI.LoginPage;
import org.example.backend.service.AppContext;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        AppContext.get().schema().initialize();
        Scene scene = new Scene(new LoginPage(primaryStage), 1100, 720);
        String css = Theme.stylesheet();
        if (css != null) scene.getStylesheets().add(css);
        primaryStage.setTitle("Clinic Portal");
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(640);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
