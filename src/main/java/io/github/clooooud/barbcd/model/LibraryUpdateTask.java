package io.github.clooooud.barbcd.model;

import java.util.Timer;
import java.util.TimerTask;

public final class LibraryUpdateTask implements Runnable {

    public static void runTask() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Thread(new LibraryUpdateTask()).start();
            }
        }, 1000 * 60 * 5, 1000 * 60 * 5);
    }

    private LibraryUpdateTask() {}

    @Override
    public void run() {

    }
}
