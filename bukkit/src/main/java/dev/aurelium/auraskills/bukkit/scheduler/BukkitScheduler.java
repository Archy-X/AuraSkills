package dev.aurelium.auraskills.bukkit.scheduler;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.enums.EntityTaskResult;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.scheduler.Scheduler;
import dev.aurelium.auraskills.common.scheduler.Task;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BukkitScheduler extends Scheduler {

    private final FoliaLib foliaLib;

    public BukkitScheduler(AuraSkills plugin) {
        super(plugin);
        this.foliaLib = new FoliaLib(plugin);
    }

    @Override
    public Task executeSync(Runnable runnable) {
        WrappedTask task = foliaLib.getScheduler().runLater(runnable, 1L);
        return new BukkitTaskWrapper(task);
    }

    @Override
    public Task scheduleSync(Runnable runnable, long delay, TimeUnit timeUnit) {
        WrappedTask task = foliaLib.getScheduler().runLater(runnable, delay, timeUnit);
        return new BukkitTaskWrapper(task);
    }

    @Override
    public Task timerSync(TaskRunnable runnable, long delay, long period, TimeUnit timeUnit) {
        WrappedTask bukkitTask = foliaLib.getScheduler().runTimer(runnable, delay, period, timeUnit);
        Task task = new BukkitTaskWrapper(bukkitTask);
        runnable.injectTask(task);
        return task;
    }

    @Override
    public Task timerAsync(TaskRunnable runnable, long delay, long period, TimeUnit timeUnit) {
        WrappedTask bukkitTask = foliaLib.getScheduler().runTimerAsync(runnable, delay, period, timeUnit);
        Task task = new BukkitTaskWrapper(bukkitTask);
        runnable.injectTask(task);
        return task;
    }

    public CompletableFuture<Void> executeAtLocation(Location location, Consumer<WrappedTask> consumer) {
        return foliaLib.getScheduler().runAtLocation(location, consumer);
    }

    public Task scheduleAtLocation(Location location, Runnable runnable, long delay, TimeUnit timeUnit) {
        WrappedTask task = foliaLib.getScheduler().runAtLocationLater(location, runnable, delay, timeUnit);
        return new BukkitTaskWrapper(task);
    }

    public Task timerAtLocation(Location location, Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        WrappedTask task = foliaLib.getScheduler().runAtLocationTimer(location, runnable, delay, period, timeUnit);
        return new BukkitTaskWrapper(task);
    }

    public CompletableFuture<EntityTaskResult> executeAtEntity(Entity entity, Consumer<WrappedTask> consumer) {
        return foliaLib.getScheduler().runAtEntity(entity, consumer);
    }

    public Task scheduleAtEntity(Entity entity, Runnable runnable, long delay, TimeUnit timeUnit) {
        WrappedTask task = foliaLib.getScheduler().runAtEntityLater(entity, runnable, delay, timeUnit);
        return new BukkitTaskWrapper(task);
    }

    public Task timerAtEntity(Entity entity, Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        WrappedTask task = foliaLib.getScheduler().runAtEntityTimer(entity, runnable, delay, period, timeUnit);
        return new BukkitTaskWrapper(task);
    }
}
