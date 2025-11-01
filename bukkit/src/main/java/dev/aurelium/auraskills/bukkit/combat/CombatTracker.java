package dev.aurelium.auraskills.bukkit.combat;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Tracks PvP combat state for players to enable equipment-only stats during combat.
 * Players enter combat when they damage or are damaged by another player.
 * Players exit combat 10 seconds after their last PvP interaction.
 */
public class CombatTracker {
    private final AuraSkills plugin;
    private final Map<UUID, Long> combatTimestamps = new ConcurrentHashMap<>();
    private static final long COMBAT_TIMEOUT_MS = 10_000; // 10 seconds

    public CombatTracker(AuraSkills plugin) {
        this.plugin = plugin;
        startCleanupTask();
    }

    /**
     * Marks a player as being in PvP combat.
     *
     * @param player the player entering combat
     */
    public void enterCombat(Player player) {
        if (!plugin.configBoolean(Option.PVP_ONLY_EQUIPMENT_STATS)) {
            return; // Feature disabled
        }

        UUID uuid = player.getUniqueId();
        boolean wasInCombat = isInCombat(player);
        combatTimestamps.put(uuid, System.currentTimeMillis());

        if (!wasInCombat) {
            // Player just entered combat, reload their traits
            plugin.getStatManager().reloadAllTraits(plugin.getUser(player));
        }
    }

    /**
     * Checks if a player is currently in PvP combat.
     *
     * @param player the player to check
     * @return true if the player is in combat, false otherwise
     */
    public boolean isInCombat(Player player) {
        if (!plugin.configBoolean(Option.PVP_ONLY_EQUIPMENT_STATS)) {
            return false; // Feature disabled, no one is in combat
        }

        Long timestamp = combatTimestamps.get(player.getUniqueId());
        if (timestamp == null) {
            return false;
        }

        long timeSinceLastCombat = System.currentTimeMillis() - timestamp;
        return timeSinceLastCombat < COMBAT_TIMEOUT_MS;
    }

    /**
     * Forces a player to exit combat state.
     *
     * @param player the player to remove from combat
     */
    public void exitCombat(Player player) {
        UUID uuid = player.getUniqueId();
        boolean wasInCombat = isInCombat(player);
        combatTimestamps.remove(uuid);

        if (wasInCombat) {
            // Player just exited combat, reload their traits
            plugin.getStatManager().reloadAllTraits(plugin.getUser(player));
        }
    }

    /**
     * Starts a background task that periodically checks for players who should exit combat.
     */
    private void startCleanupTask() {
        var task = new TaskRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                combatTimestamps.entrySet().removeIf(entry -> {
                    long timeSinceLastCombat = currentTime - entry.getValue();
                    if (timeSinceLastCombat >= COMBAT_TIMEOUT_MS) {
                        // Player's combat timer expired
                        Player player = plugin.getServer().getPlayer(entry.getKey());
                        if (player != null && player.isOnline()) {
                            // Reload traits to restore non-equipment bonuses
                            plugin.getStatManager().reloadAllTraits(plugin.getUser(player));
                        }
                        return true; // Remove from map
                    }
                    return false; // Keep in map
                });
            }
        };

        // Run every second to check for combat timeouts
        plugin.getScheduler().scheduleSync(task, 1, TimeUnit.SECONDS);
    }

    /**
     * Clears combat state for a player (called on logout).
     *
     * @param player the player logging out
     */
    public void clearCombatState(Player player) {
        combatTimestamps.remove(player.getUniqueId());
    }

    /**
     * Gets the number of seconds until the player exits combat.
     *
     * @param player the player to check
     * @return seconds remaining, or 0 if not in combat
     */
    public int getSecondsRemaining(Player player) {
        if (!isInCombat(player)) {
            return 0;
        }

        Long timestamp = combatTimestamps.get(player.getUniqueId());
        if (timestamp == null) {
            return 0;
        }

        long timeSinceLastCombat = System.currentTimeMillis() - timestamp;
        long timeRemaining = COMBAT_TIMEOUT_MS - timeSinceLastCombat;
        return (int) Math.ceil(timeRemaining / 1000.0);
    }
}

