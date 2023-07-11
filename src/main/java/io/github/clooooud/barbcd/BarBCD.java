package io.github.clooooud.barbcd;

import io.github.clooooud.barbcd.data.api.PublicCredentials;
import io.github.clooooud.barbcd.gui.StageWrapper;
import io.github.clooooud.barbcd.gui.scenes.MainScene;
import io.github.clooooud.barbcd.gui.scenes.StartScene;
import io.github.clooooud.barbcd.data.model.Library;
import io.github.clooooud.barbcd.data.model.document.Editor;
import io.github.clooooud.barbcd.data.model.document.Oeuvre;
import io.github.clooooud.barbcd.data.model.document.Categorie;
import javafx.application.Application;
import javafx.stage.Stage;

public class BarBCD extends Application {

    private StageWrapper stageWrapper;
    private PublicCredentials credentials;
    private final Library library = new Library("");

    @Override
    public void start(Stage stage) {
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setTitle("BarBCD");
        this.credentials = new PublicCredentials();
        this.stageWrapper = new StageWrapper(stage);
        this.stageWrapper.setContent(this.credentials.isFileExisted() ? new MainScene(this) : new StartScene(this));
        stage.show();
    }

    public StageWrapper getStageWrapper() {
        return stageWrapper;
    }

    public Library getLibrary() {
        return library;
    }

    public static void main(String[] args) {
        launch();
    }
}