package io.github.clooooud.barbcd.data.api.tasks;

import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.api.PublicCredentials;
import io.github.clooooud.barbcd.data.model.Library;

import java.io.IOException;

public class LoadRunnable implements Runnable {

    public static void start(Library library, PublicCredentials credentials) {
        new Thread(new LoadRunnable(library, credentials)).start();
    }

    private final Library library;
    private final PublicCredentials credentials;

    private LoadRunnable(Library library, PublicCredentials credentials) {
        this.library = library;
        this.credentials = credentials;
    }

    @Override
    public void run() {
        GSheetApi api = new GSheetApi(credentials);
        api.init();

        try {
            api.load(library);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
