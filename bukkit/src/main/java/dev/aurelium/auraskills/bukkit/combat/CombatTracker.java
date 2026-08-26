package dev.aurelium.auraskills.bukkit.combat;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.message.MessageBuilder;
import dev.aurelium.auraskills.common.message.type.CombatMessage;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import dev.aurelium.auraskills.common.user.User;
import net.kyori.adventure.text.Component;
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

        User user = plugin.getUser(player);

        boolean wasInCombat = isInCombat(user);
        combatTimestamps.put(user.getUuid(), System.currentTimeMillis());

        if (!wasInCombat) {
            // Player just entered combat, reload their traits
            plugin.getStatManager().reloadAllTraits(user);
            // Send combat entry message
            String message = MessageBuilder.create(plugin).locale(user.getLocale())
                    .rawMessage(CombatMessage.ENTER).toString();
            Component component = plugin.getMessageProvider().stringToComponent(message);
            user.sendMessage(component);
        }
    }

    /**
     * Checks if a player is currently in PvP combat.
     *
     * @param user the player to check
     * @return true if the player is in combat, false otherwise
     */
    public boolean isInCombat(User user) {
        if (!plugin.configBoolean(Option.PVP_ONLY_EQUIPMENT_STATS)) {
            return false; // Feature disabled, no one is in combat
        }

        Long timestamp = combatTimestamps.get(user.getUuid());
        if (timestamp == null) {
            return false;
        }

        long timeSinceLastCombat = System.currentTimeMillis() - timestamp;
        return timeSinceLastCombat < COMBAT_TIMEOUT_MS;
    }

    /**
     * Forces a player to exit combat state.
     *
     * @param user the player to remove from combat
     */
    public void exitCombat(User user) {
        boolean wasInCombat = isInCombat(user);
        combatTimestamps.remove(user.getUuid());

        if (wasInCombat) {
            // Player just exited combat, reload their traits
            plugin.getStatManager().reloadAllTraits(user);

            // Send combat exit message
            String message = MessageBuilder.create(plugin).locale(user.getLocale())
                    .rawMessage(CombatMessage.EXIT).toString();
            Component component = plugin.getMessageProvider().stringToComponent(message);
            user.sendMessage(component);
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
                        User user = plugin.getUserManager().getUser(entry.getKey());
                        if (user != null) {
                            // Reload traits to restore non-equipment bonuses
                            plugin.getStatManager().reloadAllTraits(user);
                            // Send combat exit message
                            String message = MessageBuilder.create(plugin).locale(user.getLocale())
                                    .rawMessage(CombatMessage.EXIT).toString();

                            Component component = plugin.getMessageProvider().stringToComponent(message);
                            user.sendMessage(component);
                        }
                        return true; // Remove from map
                    }
                    return false; // Keep in map
                });
            }
        };

        // Run every second to check for combat timeouts (delay: 1s, period: 1s)
        plugin.getScheduler().timerSync(task, 1, 1, TimeUnit.SECONDS);
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
     * @param user the player to check
     * @return seconds remaining, or 0 if not in combat
     */
    public int getSecondsRemaining(User user) {
        if (!isInCombat(user)) {
            return 0;
        }

        Long timestamp = combatTimestamps.get(user.getUuid());
        if (timestamp == null) {
            return 0;
        }

        long timeSinceLastCombat = System.currentTimeMillis() - timestamp;
        long timeRemaining = COMBAT_TIMEOUT_MS - timeSinceLastCombat;
        return (int) Math.ceil(timeRemaining / 1000.0);
    }
}
