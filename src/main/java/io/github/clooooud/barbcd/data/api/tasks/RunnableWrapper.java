package io.github.clooooud.barbcd.data.api.tasks;

public class RunnableWrapper implements Runnable {

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

    public void run(boolean async) {
        if (async) {
            run();
        } else {
            this.startRunnables();
        }
    }

    @Override
    public void run() {
        new Thread(this::startRunnables).start();
    }

    private void startRunnables() {
        for (Runnable run : new Runnable[]{before, runnable, after}) {
            if (run != null) {
                try {
                    run.run();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    break;
                }
            }
        }
    }
}
