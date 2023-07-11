package io.github.clooooud.barbcd.data.api.tasks;

import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.api.PublicCredentials;
import io.github.clooooud.barbcd.data.model.Library;

import java.io.IOException;

public class SaveRunnable implements Runnable {

    public static void start(PublicCredentials credentials, Library library, String adminPassword) {
        new Thread(new SaveRunnable(credentials, library, adminPassword)).start();
    }

    private final PublicCredentials credentials;
    private final Library library;
    private final String adminPassword;

    private SaveRunnable(PublicCredentials credentials, Library library, String adminPassword) {
        this.credentials = credentials;
        this.library = library;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run() {
        GSheetApi api = new GSheetApi(credentials);
        api.initAdmin(adminPassword);
        try {
            api.save(library);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
