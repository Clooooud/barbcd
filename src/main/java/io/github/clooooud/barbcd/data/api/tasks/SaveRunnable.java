package io.github.clooooud.barbcd.data.api.tasks;

import io.github.clooooud.barbcd.BarBCD;
import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.model.Library;
import javafx.application.Platform;

import java.io.IOException;

public class SaveRunnable implements Runnable {

    public static RunnableWrapper create(BarBCD app) {
        return new RunnableWrapper(new SaveRunnable(app.getLibrary(), app.getGSheetApi(), app.getLibrary().getAdminPassword()));
    }

    private final GSheetApi gSheetApi;
    private final Library library;
    private final String adminPassword;

    private SaveRunnable(Library library, GSheetApi gSheetApi, String adminPassword) {
        this.gSheetApi = gSheetApi;
        this.library = library;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run() {
        Platform.runLater(() -> library.getApp().getStageWrapper().getScene().startLoading());
        gSheetApi.initAdmin(adminPassword);

        try {
            gSheetApi.save(library);
        } catch (IOException e) {
            Platform.runLater(() -> library.getApp().getStageWrapper().getScene().finishLoading(false));
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> library.getApp().getStageWrapper().getScene().finishLoading(true));
    }
}
