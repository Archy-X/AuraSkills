package dev.aurelium.auraskills.bukkit;

import dev.aurelium.auraskills.bukkit.scheduler.BukkitScheduler;
import dev.aurelium.auraskills.common.scheduler.Task;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;

import java.util.concurrent.TimeUnit;

public class SyncOnlyScheduler extends BukkitScheduler {

    public SyncOnlyScheduler(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public Task executeAsync(Runnable runnable) {
        return executeSync(runnable);
    }

    @Override
    public Task scheduleAsync(Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduleSync(runnable, delay, timeUnit);
    }

    @Override
    public Task timerAsync(TaskRunnable runnable, long delay, long period, TimeUnit timeUnit) {
        return timerSync(runnable, delay, period, timeUnit);
    }

}
