package dev.auramc.auraskills.common.scheduler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.auramc.auraskills.common.AuraSkillsPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private final AuraSkillsPlugin plugin;

    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder().setNameFormat("auraskills-async-task-%d").build());
    private final ScheduledExecutorService syncScheduler = Executors.newSingleThreadScheduledExecutor((r) -> Thread.currentThread());
    private final ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(0,
            new ThreadFactoryBuilder().setNameFormat("auraskills-async-scheduler-%d").build());

    public Scheduler(final AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    public Task executeAsync(final Runnable runnable) {
        return new SubmittedTask(asyncExecutor.submit(runnable));
    }

    public Task scheduleSync(final Runnable runnable, final long delay, final TimeUnit timeUnit) {
        return new ScheduledTask(syncScheduler.schedule(runnable, delay, timeUnit));
    }

    public Task scheduleAsync(final Runnable runnable, final long delay, final TimeUnit timeUnit) {
        return new ScheduledTask(asyncScheduler.schedule(runnable, delay, timeUnit));
    }

    // Should be run by the implementation when server is shutdown
    public void shutdown() {
        asyncExecutor.shutdown();
        syncScheduler.shutdown();
        asyncScheduler.shutdown();

        try {
            boolean asyncExecutorDone = asyncExecutor.awaitTermination(2, TimeUnit.SECONDS);
            boolean syncSchedulerDone = syncScheduler.awaitTermination(2, TimeUnit.SECONDS);
            boolean asyncSchedulerDone = asyncScheduler.awaitTermination(2, TimeUnit.SECONDS);

            if (!asyncExecutorDone || !syncSchedulerDone || !asyncSchedulerDone) {
                plugin.logger().warn("Scheduler had incomplete tasks when shutting down");
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

}
