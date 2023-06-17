package dev.aurelium.auraskills.common.scheduler;

import java.util.concurrent.Future;

public class SubmittedTask implements Task {

    private final Future<?> future;

    public SubmittedTask(final Future<?> future) {
        this.future = future;
    }

    @Override
    public TaskStatus getStatus() {
        return future.isDone() ? TaskStatus.STOPPED : TaskStatus.RUNNING;
    }

    @Override
    public void cancel() {
        future.cancel(false);
    }
}
