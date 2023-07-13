package io.github.clooooud.barbcd.data.api.tasks;

import io.github.clooooud.barbcd.data.api.GSheetApi;
import io.github.clooooud.barbcd.data.api.PublicCredentials;
import io.github.clooooud.barbcd.data.model.Library;

import java.io.IOException;

public class LoadRunnable implements Runnable {

    public static RunnableWrapper create(Library library, GSheetApi gSheetApi) {
        return new RunnableWrapper(new LoadRunnable(library, gSheetApi));
    }

    private final Library library;
    private final GSheetApi gSheetApi;

    public LoadRunnable(Library library, GSheetApi gSheetApi) {
        this.library = library;
        this.gSheetApi = gSheetApi;
    }

    @Override
    public void run() {
        try {
            gSheetApi.load(library);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
