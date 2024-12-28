package dev.aurelium.auraskills.common.storage;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.AntiAfkLog;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.user.UserManager;
import dev.aurelium.auraskills.common.user.UserState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class StorageProvider {

    private static final long SAVE_TIMEOUT_MS = 2000;
    private static final long LOAD_TIMEOUT_MS = 2000;

    public final AuraSkillsPlugin plugin;
    public final UserManager userManager;
    private final ConcurrentHashMap<UUID, ReentrantReadWriteLock> userLocks = new ConcurrentHashMap<>();

    public StorageProvider(AuraSkillsPlugin plugin) {
        this.userManager = plugin.getUserManager();
        this.plugin = plugin;
    }

    public void load(UUID uuid) throws Exception {
        ReentrantReadWriteLock lock = getUserLock(uuid);
        boolean lockAcquired = false;
        try {
            lockAcquired = lock.readLock().tryLock(LOAD_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!lockAcquired) {
                plugin.logger().warn("Load timout exceeded for user " + uuid);
            }

            User user = loadRaw(uuid);
            fixInvalidData(user);

            plugin.getUserManager().addUser(user);

            plugin.getScheduler().executeSync(() -> {
                plugin.getStatManager().updateStats(user); // Update stats
                plugin.getEventHandler().callUserLoadEvent(user); // Call event
            });

            // Update permissions
            plugin.getRewardManager().updatePermissions(user);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lockAcquired) {
                lock.readLock().unlock();
            }
            removeUserLock(uuid, lock);
        }
    }

    protected abstract User loadRaw(UUID uuid) throws Exception;

    /**
     * Loads a snapshot of player data for an offline player
     *
     * @param uuid The uuid of the player
     * @return A PlayerDataState containing a snapshot of player data
     */
    @NotNull
    public abstract UserState loadState(UUID uuid) throws Exception;

    /**
     * Applies the given PlayerData state to storage. Will override
     * previously saved data.
     *
     * @param state The state to apply, where the uuid is the same uuid the data is applied to.
     */
    public abstract void applyState(UserState state) throws Exception;

    public void saveSafely(@NotNull User user) {
        ReentrantReadWriteLock lock = getUserLock(user.getUuid());
        boolean lockAcquired = false;
        try {
            lockAcquired = lock.writeLock().tryLock(SAVE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!lockAcquired) {
                plugin.logger().warn("Save timeout exceeded for user " + user.getUuid());
                return;
            }
            save(user);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lockAcquired) {
                lock.writeLock().unlock();
            }
            removeUserLock(user.getUuid(), lock);
        }
    }

    public abstract void save(@NotNull User user) throws Exception;

    public abstract void delete(UUID uuid) throws Exception;

    public abstract List<UserState> loadStates(boolean ignoreOnline, boolean skipKeyValues) throws Exception;

    public abstract List<AntiAfkLog> loadAntiAfkLogs(UUID uuid);

    public void startAutoSaving() {
        if (!plugin.configBoolean(Option.AUTO_SAVE_ENABLED)) {
            return;
        }
        long interval = plugin.configInt(Option.AUTO_SAVE_INTERVAL_TICKS);
        var task = new TaskRunnable() {
            @Override
            public void run() {
                for (User user : userManager.getOnlineUsers()) {
                    try {
                        saveSafely(user);
                    } catch (Exception e) {
                        plugin.logger().warn("Error running auto-save on user data:");
                        e.printStackTrace();
                    }
                }
            }
        };
        plugin.getScheduler().timerAsync(task, interval * 50, interval * 50, TimeUnit.MILLISECONDS);
    }

    private void fixInvalidData(User user) {
        // Ensure users are at least the start level
        int startLevel = plugin.config().getStartLevel();
        for (Skill skill : user.getSkillLevelMap().keySet()) {
            if (user.getSkillLevel(skill) < startLevel) {
                user.setSkillLevel(skill, startLevel);
            }
        }
        // Correct over max level
        if (plugin.configBoolean(Option.DATA_VALIDATION_CORRECT_OVER_MAX_LEVEL)) {
            for (Skill skill : user.getSkillLevelMap().keySet()) {
                int maxLevel = skill.getMaxLevel();
                if (user.getSkillLevelMap().get(skill) > maxLevel) {
                    user.setSkillLevel(skill, maxLevel);
                }
            }
        }
    }

    protected ReentrantReadWriteLock getUserLock(UUID uuid) {
        return userLocks.computeIfAbsent(uuid, id -> new ReentrantReadWriteLock());
    }

    protected void removeUserLock(UUID uuid, ReentrantReadWriteLock lock) {
        if (lock.getReadLockCount() == 0 && !lock.isWriteLocked()) {
            userLocks.remove(uuid);
        }
    }

}
