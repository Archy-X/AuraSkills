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
import java.util.concurrent.TimeUnit;

public abstract class StorageProvider {

    public final AuraSkillsPlugin plugin;
    public final UserManager userManager;

    public StorageProvider(AuraSkillsPlugin plugin) {
        this.userManager = plugin.getUserManager();
        this.plugin = plugin;
    }

    public void load(UUID uuid) throws Exception {
        User user = loadRaw(uuid);
        fixInvalidData(user);

        plugin.getUserManager().addUser(user);

        // Update stats
        plugin.getStatManager().updateStats(user);

        // Call event
        plugin.getScheduler().executeSync(() -> plugin.getEventHandler().callUserLoadEvent(user));

        // Update permissions
        plugin.getRewardManager().updatePermissions(user);
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
                        if (user.isSaving()) {
                            continue;
                        }
                        save(user);
                    } catch (Exception e) {
                        user.setSaving(false);
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

}
