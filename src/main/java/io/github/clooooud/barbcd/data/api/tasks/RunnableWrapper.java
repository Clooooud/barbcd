package io.github.clooooud.barbcd.data.api.tasks;

import java.util.List;

public class RunnableWrapper {

    private Runnable before;
    private Runnable runnable;
    private Runnable after;

    public RunnableWrapper(Runnable runnable) {
        this.runnable = runnable;
    }

    public RunnableWrapper then(Runnable after) {
        this.after = after;
        return this;
    }

    public RunnableWrapper before(Runnable before) {
        this.before = before;
        return this;
    }

    public void start() {
        new Thread(() -> {
            for (Runnable run : new Runnable[]{before, runnable, after}) {
                if (run != null) {
                    run.run();
                }
            }
        }).start();
    }
}
