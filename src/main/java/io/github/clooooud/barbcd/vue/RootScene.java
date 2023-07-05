package io.github.clooooud.barbcd.vue;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.javafx.Style;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;

public class RootScene extends Scene {

    public static InputStream getResource(String url) {
        return RootScene.class.getResourceAsStream(url);
    }

    protected BarBCD app;
    protected HBox headerBox;

    public RootScene(BarBCD app) {
        super(new VBox());
        this.app = app;
        this.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        initHeader();
    }

    private VBox getParent() {
        return (VBox) this.getRoot();
    }

    private void initHeader() {
        this.headerBox = new HBox();
        headerBox.setMinHeight(100);
        headerBox.setPrefHeight(100);
        headerBox.setMaxHeight(100);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 20, 0, 20));
        headerBox.getStyleClass().add("header-bg");

        Label label = new Label("BarBCD");
        label.setFont(Font.font("Roboto", 48));
        label.setTextFill(Color.WHITE);
        label.setMaxWidth(Double.MAX_VALUE);

        headerBox.getChildren().add(label);
        HBox.setHgrow(label, Priority.ALWAYS);

        // TODO: connecté pas connecté, variation de bouton

        Button button = new Button("Administration");
        button.setTextFill(Color.WHITE);
        button.setFont(Font.font("Roboto", FontWeight.BOLD, 14));
        button.setStyle(new Style()
                .set("-fx-background-color: transparent;")
                .set("-fx-border-color: derive(#97719d, -20%);")
                .set("-fx-border-radius: 20;")
                .set("-fx-background-radius: 20;")
                .toString()
        );
        button.setPrefHeight(40);
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphic(new ImageView(new Image(getResource("assets/lock.png"))));
        button.setOnMouseEntered(event -> {
            button.setStyle(Style.get(button.getStyle()).set("-fx-background-color: derive(#97719d, -20%);").toString());
        });
        button.setOnMouseExited(event -> {
            button.setStyle(Style.get(button.getStyle()).set("-fx-background-color: transparent").toString());
        });

        headerBox.getChildren().add(button);

        this.getParent().getChildren().add(headerBox);
    }


}
