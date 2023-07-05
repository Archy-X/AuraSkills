package dev.aurelium.auraskills.bukkit.scheduler;

import dev.aurelium.auraskills.common.scheduler.Task;
import dev.aurelium.auraskills.common.scheduler.TaskStatus;
import org.bukkit.scheduler.BukkitTask;

public class BukkitTaskWrapper implements Task {

    private final BukkitTask bukkitTask;

    public BukkitTaskWrapper(BukkitTask bukkitTask) {
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
