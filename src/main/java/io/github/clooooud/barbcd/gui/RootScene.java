package io.github.clooooud.barbcd.gui;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.gui.content.ContentBox;
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

public class RootScene extends Scene {

    public static InputStream getResource(String url) {
        return RootScene.class.getResourceAsStream(url);
    }

    protected BarBCD app;
    protected HBox headerBox;
    protected ContentBox contentBox;

    public RootScene(BarBCD app) {
        this(app, null);
    }

    public RootScene(BarBCD app, ContentBox contentBox) {
        super(new VBox());
        this.app = app;
        this.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());

        initHeader();
        setAndUpdateContent(contentBox);
    }

    public void setAndUpdateContent(ContentBox contentBox) {
        this.contentBox = contentBox;
        updateContent();
    }

    private void updateContent() {
        getParent().getChildren().setAll(Stream.of(getParent().getChildren().get(0), this.contentBox == null ? null : this.contentBox.getContent())
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        if (getParent().getChildren().size() >= 2) {
            Node node = getParent().getChildren().get(1);
            node.setId("page-content");
            VBox.setVgrow(node, Priority.ALWAYS);
        }
    }

    private VBox getParent() {
        return (VBox) this.getRoot();
    }

    private void initHeader() {
        this.headerBox = new HBox();
        headerBox.setMinHeight(100);
        headerBox.setPrefHeight(100);
        headerBox.setMaxHeight(100);
        headerBox.setId("header-box");

        Label label = new Label("BarBCD");
        label.setId("title");
        label.setMaxWidth(Double.MAX_VALUE);

        headerBox.getChildren().add(label);
        HBox.setHgrow(label, Priority.ALWAYS);

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
