package dev.aurelium.auraskills.common.scheduler;

public abstract class TaskRunnable implements Runnable {

    private Task task;

    public TaskRunnable() {

    }

    public synchronized void cancel() throws IllegalStateException {
        task.cancel();
    }

    public void injectTask(Task task) {
        this.task = task;
    }

}
