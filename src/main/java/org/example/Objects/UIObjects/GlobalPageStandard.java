package org.example.Objects.UIObjects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.UI.LoginPage;
import org.example.backend.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The app shell shared by all three roles: a slim sidebar (navigation + sign out), a breadcrumb header and a scrolling
 * content area. A role page only registers its sections with addNav(...) and calls openNav(0).
 */
public class GlobalPageStandard extends BorderPane {
    protected final Stage stage;
    protected final VBox contentArea = new VBox(20);

    private final String roleTitle;
    private final VBox navBox = new VBox(2);
    private final Label crumb = Theme.text("", 13, Theme.TEXT, true);
    private final List<HBox> navItems = new ArrayList<>();
    private final List<String> navNames = new ArrayList<>();
    private final List<Supplier<Node>> navPages = new ArrayList<>();
    private int active = -1;

    public GlobalPageStandard(Stage primaryStage, String role) {
        this.stage = primaryStage;
        this.roleTitle = role.substring(0, 1).toUpperCase() + role.substring(1);
        setPrefSize(1100, 720);
        setStyle(Theme.rootStyle());

        contentArea.setPadding(new Insets(28, 32, 32, 32));
        ScrollPane scroll = new ScrollPane(contentArea);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + Theme.BG + "; -fx-background-color: " + Theme.BG + "; -fx-border-color: transparent;");

        VBox right = new VBox(buildHeader(), scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        setLeft(buildSidebar());
        setCenter(right);
    }

    // ---------- API for role pages ----------
    protected void addNav(String label, Supplier<Node> page) {
        int index = navItems.size();
        Label text = Theme.text(label, 13, Theme.TEXT_2, false);
        HBox item = new HBox(text);
        item.setPadding(new Insets(7, 10, 7, 10));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setOnMouseClicked(e -> openNav(index));
        item.setOnMouseEntered(e -> { if (index != active) item.setStyle(navStyle(false, true)); });
        item.setOnMouseExited(e -> { if (index != active) item.setStyle(navStyle(false, false)); });
        item.setStyle(navStyle(false, false));
        navItems.add(item);
        navNames.add(label);
        navPages.add(page);
        navBox.getChildren().add(item);
    }

    protected void openNav(int index) {
        active = index;
        for (int i = 0; i < navItems.size(); i++) {
            boolean on = i == index;
            navItems.get(i).setStyle(navStyle(on, false));
            ((Label) navItems.get(i).getChildren().get(0)).setStyle("-fx-font-size: 13px; -fx-font-weight: " + (on ? "600" : "normal")
                    + "; -fx-text-fill: " + (on ? Theme.ACCENT_TEXT : Theme.TEXT_2) + ";");
        }
        crumb.setText("Clinic Portal  /  " + roleTitle + "  /  " + navNames.get(index));
        contentArea.getChildren().setAll(navPages.get(index).get());
    }

    /** Rebuilds the current section so it shows fresh data. */
    protected void refresh() {
        if (active >= 0) openNav(active);
    }

    protected boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.setTitle("Please confirm");
        alert.initOwner(stage);
        alert.getDialogPane().setStyle("-fx-background-color: " + Theme.SURFACE + ";");
        String css = Theme.stylesheet();
        if (css != null) alert.getDialogPane().getStylesheets().add(css);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    protected void signOut() {
        Session.id = 0;
        Session.role = null;
        stage.getScene().setRoot(new LoginPage(stage));
    }

    // ---------- shell pieces ----------
    private static String navStyle(boolean on, boolean hover) {
        return "-fx-background-radius: 6; -fx-cursor: hand; -fx-background-color: "
                + (on ? Theme.ACCENT_SOFT : hover ? "#1A1A1F" : "transparent") + ";";
    }

    private HBox buildHeader() {
        HBox header = new HBox(12, crumb, Theme.spacer(), Theme.lozenge(roleTitle, Theme.Tone.BLUE));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 24, 14, 24));
        header.setStyle("-fx-background-color: " + Theme.SURFACE + "; -fx-border-color: transparent transparent " + Theme.BORDER
                + " transparent; -fx-border-width: 0 0 1 0;");
        return header;
    }

    private VBox buildSidebar() {
        HBox brand = new HBox(10, Theme.brandMark(26, false), Theme.text("Clinic Portal", 15, Theme.TEXT, true));
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(4, 6, 12, 6));

        StyledButton signOut = new StyledButton("Sign out", StyledButton.Kind.GHOST);
        signOut.setMaxWidth(Double.MAX_VALUE);
        signOut.setOnAction(e -> signOut());

        HBox me = new HBox(10, Theme.avatar(roleTitle, 30),
                new VBox(1, Theme.text(roleTitle, 13, Theme.TEXT, true), Theme.text("Signed in", 11, Theme.TEXT_3, false)));
        me.setAlignment(Pos.CENTER_LEFT);
        me.setPadding(new Insets(8, 6, 4, 6));

        VBox sidebar = new VBox(8, brand, Theme.text("MENU", 11, Theme.TEXT_3, true), navBox, Theme.spacer(), me, signOut);
        sidebar.setPadding(new Insets(16, 12, 16, 12));
        sidebar.setPrefWidth(236);
        sidebar.setMinWidth(236);
        sidebar.setStyle("-fx-background-color: #0D0D0F; -fx-border-color: transparent " + Theme.BORDER + " transparent transparent; -fx-border-width: 0 1 0 0;");
        return sidebar;
    }
}
