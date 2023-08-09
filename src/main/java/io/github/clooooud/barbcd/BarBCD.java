package io.github.clooooud.barbcd;

import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.api.PublicCredentials;
import io.github.clooooud.barbcd.data.api.tasks.LoadRunnable;
import io.github.clooooud.barbcd.data.model.Library;
import io.github.clooooud.barbcd.gui.StageWrapper;
import io.github.clooooud.barbcd.gui.scenes.MainScene;
import io.github.clooooud.barbcd.gui.scenes.StartScene;
import javafx.application.Application;
import javafx.stage.Stage;

public class BarBCD extends Application {

    private final PublicCredentials credentials = new PublicCredentials();
    private final Library library = new Library(this, " ");
    private final GSheetApi gSheetApi = new GSheetApi(credentials);
    private StageWrapper stageWrapper;

    @Override
    public void start(Stage stage) {
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setTitle("BarBCD");
        this.stageWrapper = new StageWrapper(stage);
        boolean isInitialized = this.credentials.isFileExisted() && !this.credentials.isEmpty();
        if (isInitialized) {
            LoadRunnable.create(library, gSheetApi)
                    .then(() -> this.getLibrary().createUser("test", "mdp", "test"))
                    .run();
        }
        this.stageWrapper.setContent(isInitialized ? new MainScene(this) : new StartScene(this));
        stage.show();
    }

    public GSheetApi getGSheetApi() {
        return gSheetApi;
    }

    public PublicCredentials getCredentials() {
        return credentials;
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