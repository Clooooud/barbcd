package io.github.clooooud.barbcd;

import io.github.clooooud.barbcd.gui.RootScene;
import io.github.clooooud.barbcd.gui.content.MainPage;
import javafx.application.Application;
import javafx.stage.Stage;

public class BarBCD extends Application {

    private String adminPassword = "";

    @Override
    public void start(Stage stage) {
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle("BarBCD");
        RootScene rootScene = new RootScene(this);
        rootScene.setAndUpdateContent(new MainPage());
        stage.setScene(rootScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public boolean isAdmin() {
        return !adminPassword.isEmpty();
    }
}