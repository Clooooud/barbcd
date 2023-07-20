package io.github.clooooud.barbcd.gui;

import io.github.clooooud.barbcd.gui.scenes.RootScene;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;

public class StageWrapper {

    public static InputStream getResource(String url) {
        return StageWrapper.class.getResourceAsStream(url);
    }

    private Stage stage;
    private RootScene scene;

    public StageWrapper(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public RootScene getScene() {
        return scene;
    }

    public void setContent(RootScene scene) {
        if (this.scene != null) {
            this.scene.onSceneLeft();
        }

        this.scene = scene;

        if (stage.getScene() == null) {
            stage.setScene(new Scene(new VBox()));
            stage.getScene().getStylesheets().add(StageWrapper.class.getResource("style.css").toExternalForm());
        }

        VBox root = (VBox) stage.getScene().getRoot();
        root.setId("page-content");
        root.getChildren().clear();
        VBox vBox = new VBox();
        root.getChildren().addAll(scene.getHeader(), vBox);
        VBox.setVgrow(vBox, Priority.ALWAYS);
        Platform.runLater(() -> {
            scene.initContent(vBox);
        });
    }
}
