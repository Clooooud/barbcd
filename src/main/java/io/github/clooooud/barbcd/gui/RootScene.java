package io.github.clooooud.barbcd.gui;

import io.github.clooooud.barbcd.BarBCD;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class RootScene extends Scene {

    public static InputStream getResource(String url) {
        return RootScene.class.getResourceAsStream(url);
    }

    protected BarBCD app;
    protected HBox headerBox;
    protected HBox clickableTitle;

    public RootScene(BarBCD app) {
        super(new VBox());
        this.app = app;
        this.getStylesheets().add(RootScene.class.getResource("style.css").toExternalForm());

        initHeader();
        updateContent();
    }

    public void updateContent() {
        getParent().getChildren().clear();

        Platform.runLater(() -> {
            Node content = this.getContent();
            getParent().getChildren().setAll(Stream.of(headerBox, content)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            content.setId("page-content");
            VBox.setVgrow(content, Priority.ALWAYS);
        });
    }

    protected abstract Node getContent();

    private VBox getParent() {
        return (VBox) this.getRoot();
    }

    private void initHeader() {
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
        clickableTitle.setOnMouseClicked(event -> updateContent());

        clickableTitle.getChildren().add(label);
        labelBox.getChildren().add(clickableTitle);
        headerBox.getChildren().add(labelBox);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        // TODO: connecté pas connecté, variation de bouton

        Button button = new Button("Administration");
        button.setFocusTraversable(false);
        button.setId("header-auth-btn");
        button.setPrefHeight(40);
        button.setGraphic(new ImageView(new Image(getResource("assets/lock.png"))));

        headerBox.getChildren().add(button);

        this.getParent().getChildren().add(headerBox);
    }


}
