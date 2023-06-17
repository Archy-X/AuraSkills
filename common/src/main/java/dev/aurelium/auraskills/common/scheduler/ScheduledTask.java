package dev.aurelium.auraskills.common.scheduler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledTask implements Task {

    private final ScheduledFuture<?> future;

    public ScheduledTask(final ScheduledFuture<?> future) {
        this.future = future;
    }

    @Override
    public TaskStatus getStatus() {
        if (future.getDelay(TimeUnit.MILLISECONDS) > 0) {
            return TaskStatus.SCHEDULED;
        } else if (!future.isDone()) {
            return TaskStatus.RUNNING;
        } else {
            return TaskStatus.STOPPED;
        }
    }

    @Override
    public void cancel() {
        future.cancel(false);
    }
}
