package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.model.Library;
import io.github.clooooud.barbcd.gui.scenes.admin.MainAdminScene;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static io.github.clooooud.barbcd.gui.StageWrapper.getResource;

public abstract class RootScene {

    private static void markNodeErrorStatus(Node node, boolean isValid) {
        if (isValid) {
            node.setStyle(null);
        } else {
            node.setStyle("-fx-control-inner-background: f8d7da");
        }
    }

    public static boolean validateNonEmptyTextField(TextField textField) {
        boolean isValid = textField.getText().strip().length() > 0;

        markNodeErrorStatus(textField, isValid);

        return isValid;
    }

    private final BarBCD app;
    private HBox headerBox;
    private HBox clickableTitle;
    private Button authButton;

    public RootScene(BarBCD app) {
        this.app = app;
    }

    protected BarBCD getApp() {
        return app;
    }

    protected HBox getHeaderBox() {
        return headerBox;
    }

    protected HBox getClickableTitle() {
        return clickableTitle;
    }

    protected Button getAuthButton() {
        return authButton;
    }

    protected Library getLibrary() {
        return app.getLibrary();
    }

    public abstract void initContent(VBox vBox);

    public void onSceneLeft() {

    }

    protected void homeButtonAction(MouseEvent event) {
        this.app.getStageWrapper().setContent(this.getLibrary().isLoggedIn() ? new MainAdminScene(app) : new MainScene(app));
    }

    protected void authButtonAction(ActionEvent event) {
        if (this.getLibrary().isLoggedIn()) {
            this.getApp().getGSheetApi().closeAdminMode();
            this.getLibrary().disconnectUser();
            this.app.getStageWrapper().setContent(new MainScene(app));
        } else {
            this.app.getStageWrapper().setContent(new AuthScene(app));
        }
    }

    public void updateHeader() {
        if (this.headerBox == null) {
            this.headerBox = new HBox();
        } else {
            this.headerBox.getChildren().clear();
        }

        headerBox.setMinHeight(100);
        headerBox.setPrefHeight(100);
        headerBox.setMaxHeight(100);
        headerBox.setId("header-box");

        HBox labelBox = new HBox();
        this.clickableTitle = new HBox();
        clickableTitle.setAlignment(Pos.CENTER_LEFT);
        labelBox.setAlignment(Pos.CENTER_LEFT);
        String name = this.getLibrary().getName();
        Label titleLabel = new Label("BarBCD" + (name.isBlank() ? "" : (" " + name)));
        titleLabel.setId("title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        clickableTitle.setCursor(Cursor.HAND);
        clickableTitle.setOnMouseClicked(this::homeButtonAction);

        clickableTitle.getChildren().add(titleLabel);
        labelBox.getChildren().add(clickableTitle);
        headerBox.getChildren().add(labelBox);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        authButton = new Button(this.getLibrary().isLoggedIn() ? "Déconnexion" : "Administration");
        authButton.setFocusTraversable(false);
        authButton.setId("header-auth-btn");
        authButton.setPrefHeight(40);
        authButton.setGraphic(new ImageView(new Image(getResource(this.getLibrary().isLoggedIn() ? "assets/unlock.png" : "assets/lock.png"))));
        authButton.setOnAction(this::authButtonAction);

        headerBox.getChildren().add(authButton);
    }

    public HBox getHeader() {
        if (headerBox == null) {
            updateHeader();
        }

        return headerBox;
    }
}
