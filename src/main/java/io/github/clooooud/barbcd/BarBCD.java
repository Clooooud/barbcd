package io.github.clooooud.barbcd;

import io.github.clooooud.barbcd.vue.RootScene;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class BarBCD extends Application {

    private String adminPassword = "";

    @Override
    public void start(Stage stage) {
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle("BarBCD");
        stage.setScene(new RootScene(this));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public boolean isAdmin() {
        return !adminPassword.isEmpty();
    }
}