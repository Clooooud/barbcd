package io.github.clooooud.barbcd.data.api.tasks;

import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.model.Library;

import java.io.IOException;

public class SaveRunnable implements Runnable {

    public static void start(Library library, GSheetApi gSheetApi, String adminPassword) {
        new Thread(new SaveRunnable(library, gSheetApi, adminPassword)).start();
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
        gSheetApi.initAdmin(adminPassword);

        try {
            gSheetApi.save(library);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
