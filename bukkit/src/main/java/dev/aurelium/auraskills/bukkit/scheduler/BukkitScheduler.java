package dev.aurelium.auraskills.bukkit.scheduler;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.scheduler.Scheduler;
import dev.aurelium.auraskills.common.scheduler.Task;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

public class BukkitScheduler extends Scheduler {

    private final AuraSkills plugin;

    public BukkitScheduler(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public Task executeSync(Runnable runnable) {
        BukkitTask task = plugin.getServer().getScheduler().runTask(plugin, runnable);
        return new BukkitTaskWrapper(task);
    }

    @Override
    public Task scheduleSync(Runnable runnable, long delay, TimeUnit timeUnit) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, runnable, timeUnit.toMillis(delay) / 50);
        return new BukkitTaskWrapper(task);
    }

    @Override
    public Task timerSync(TaskRunnable runnable, long delay, long period, TimeUnit timeUnit) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, timeUnit.toMillis(delay) / 50, timeUnit.toMillis(period) / 50);
        Task task =  new BukkitTaskWrapper(bukkitTask);
        runnable.injectTask(task);
        return task;
    }

    @Override
    public Task timerAsync(TaskRunnable runnable, long delay, long period, TimeUnit timeUnit) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, timeUnit.toMillis(delay) / 50, timeUnit.toMillis(period) / 50);
        Task task = new BukkitTaskWrapper(bukkitTask);
        runnable.injectTask(task);
        return task;
    }
}
