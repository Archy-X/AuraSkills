package dev.aurelium.auraskills.bukkit.scheduler;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import dev.aurelium.auraskills.common.scheduler.Task;
import dev.aurelium.auraskills.common.scheduler.TaskStatus;

public class BukkitTaskWrapper implements Task {

    private final WrappedTask bukkitTask;

    public BukkitTaskWrapper(WrappedTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    @Override
    public TaskStatus getStatus() {
        if (bukkitTask.isCancelled()) {
            return TaskStatus.STOPPED;
        } else {
            return TaskStatus.SCHEDULED;
        }
    }

    @Override
    public void cancel() {
        bukkitTask.cancel();
    }

}
