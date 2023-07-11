package io.github.clooooud.barbcd.gui.scenes;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.model.Library;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static io.github.clooooud.barbcd.gui.StageWrapper.getResource;

public abstract class RootScene {

    private BarBCD app;
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

    public HBox getHeader() {
        this.headerBox = new HBox();
        headerBox.setMinHeight(100);
        headerBox.setPrefHeight(100);
        headerBox.setMaxHeight(100);
        headerBox.setId("header-box");

        HBox labelBox = new HBox();
        this.clickableTitle = new HBox();
        clickableTitle.setAlignment(Pos.CENTER_LEFT);
        labelBox.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label("BarBCD");
        label.setId("title");
        label.setMaxWidth(Double.MAX_VALUE);
        clickableTitle.setCursor(Cursor.HAND);
        clickableTitle.setOnMouseClicked(event -> app.getStageWrapper().setContent(new MainScene(app)));

        clickableTitle.getChildren().add(label);
        labelBox.getChildren().add(clickableTitle);
        headerBox.getChildren().add(labelBox);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        // TODO: connecté pas connecté, variation de bouton

        authButton = new Button("Administration");
        authButton.setFocusTraversable(false);
        authButton.setId("header-auth-btn");
        authButton.setPrefHeight(40);
        authButton.setGraphic(new ImageView(new Image(getResource("assets/lock.png"))));
        authButton.setOnAction(event -> {
            this.app.getStageWrapper().setContent(new AuthScene(app));
        });

        headerBox.getChildren().add(authButton);

        return headerBox;
    }
}
