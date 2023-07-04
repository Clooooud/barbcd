package io.github.clooooud.barbcd.model;

public final class LibraryUpdateTask implements Runnable {

    public static void runTask() {
        new Thread(new LibraryUpdateTask()).start();
    }

    private LibraryUpdateTask() {

    }

    @Override
    public void run() {

    }
}
