package dev.aurelium.auraskills.bukkit.ui;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.hooks.PlaceholderHook;
import dev.aurelium.auraskills.common.message.type.ActionBarMessage;
import dev.aurelium.auraskills.common.util.math.BigNumber;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.math.RomanNumber;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BossBarManager implements Listener {

    private final Map<UUID, Map<Skill, BossBar>> bossBars;
    private final Map<UUID, Map<Skill, Integer>> currentActions;
    private final Map<UUID, Map<Skill, Integer>> checkCurrentActions;
    private final Map<UUID, BossBar> singleBossBars;
    private final Map<UUID, Integer> singleCurrentActions;
    private final Map<UUID, Integer> singleCheckCurrentActions;
    private String mode;
    private int stayTime;
    private Map<Skill, BossBar.Color> colors;
    private Map<Skill, BossBar.Overlay> overlays;
    private final NumberFormat nf = new DecimalFormat("#.#");
    private final AuraSkills plugin;

    public BossBarManager(AuraSkills plugin) {
        this.bossBars = new HashMap<>();
        this.currentActions = new HashMap<>();
        this.plugin = plugin;
        this.singleBossBars = new HashMap<>();
        this.singleCurrentActions = new HashMap<>();
        this.checkCurrentActions = new HashMap<>();
        this.singleCheckCurrentActions = new HashMap<>();
    }

    public void loadOptions() {
        mode = plugin.configString(Option.BOSS_BAR_MODE);
        stayTime = plugin.configInt(Option.BOSS_BAR_STAY_TIME);
        colors = new HashMap<>();
        overlays = new HashMap<>();
        for (String entry : plugin.configStringList(Option.BOSS_BAR_FORMAT)) {
            String[] splitEntry = entry.split(" ");
            Skill skill;
            BossBar.Color color = BossBar.Color.GREEN;
            BossBar.Overlay overlay = BossBar.Overlay.PROGRESS;
            try {
                skill = plugin.getSkillRegistry().get(NamespacedId.fromDefault(splitEntry[0].toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                plugin.logger().warn("Error loading boss bar format in config.yml: " + splitEntry[0] + " is not a valid Skill");
                skill = Skills.FARMING;
            }

            if (splitEntry.length > 1) {
                try {
                    color = BossBar.Color.valueOf(splitEntry[1].toUpperCase(Locale.ROOT));
                }
                catch (IllegalArgumentException e) {
                    plugin.logger().warn("Error loading boss bar format in config.yml: " + splitEntry[0] + " is not a valid BarColor");
                }
                if (splitEntry.length > 2) {
                    try {
                        overlay = BossBar.Overlay.valueOf(splitEntry[2].toUpperCase(Locale.ROOT));
                    }
                    catch (IllegalArgumentException e) {
                        plugin.logger().warn("Error loading boss bar format in config.yml: " + splitEntry[0] + " is not a valid BarStyle");
                    }
                }
            }
            colors.put(skill, color);
            overlays.put(skill, overlay);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            Audience audience = plugin.getAudiences().player(player);
            for (Map.Entry<UUID, BossBar> entry : singleBossBars.entrySet()) {
                audience.hideBossBar(entry.getValue());
            }
            for (Map.Entry<UUID, Map<Skill, BossBar>> entry : bossBars.entrySet()) {
                Map<Skill, BossBar> bossBars = entry.getValue();
                for (Map.Entry<Skill, BossBar> bossBarEntry : bossBars.entrySet()) {
                    audience.hideBossBar(bossBarEntry.getValue());
                }
            }
        }
        bossBars.clear();
        singleBossBars.clear();
    }

    public void sendBossBar(Player player, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed) {
        UUID playerId = player.getUniqueId();
        if (maxed && !plugin.configBoolean(Option.BOSS_BAR_DISPLAY_MAXED)) { // display-maxed option
            return;
        }

        incrementAction(player, skill);
        int currentAction = getCurrentAction(player, skill);
        if (currentAction == -1 || currentAction % plugin.configInt(Option.BOSS_BAR_UPDATE_EVERY) != 0) {
            return;
        }

        BossBar bossBar;
        // Single Mode
        if (mode.equals("single")) {
            bossBar = singleBossBars.get(playerId);
        } else {
            if (!bossBars.containsKey(playerId)) bossBars.put(playerId, new HashMap<>());
            bossBar = bossBars.get(playerId).get(skill);
        }
        // If player does not have a boss bar in that skill
        if (bossBar == null) {
            bossBar = handleNewBossBar(player, skill, currentXp, levelXp, xpGained, level, maxed);
        }
        // Use existing one
        else {
            handleExistingBossBar(bossBar, player, skill, currentXp, levelXp, xpGained, level, maxed);
        }
        // Increment current action
        if (mode.equals("single")) {
            singleCurrentActions.compute(playerId, (id, num) -> (num == null) ? 0 : num + 1);
        } else {
            currentActions.computeIfAbsent(playerId, k -> new HashMap<>()).compute(skill, (sk, num) -> (num == null) ? 0 : num + 1);
        }
        scheduleHide(playerId, skill, bossBar); // Schedule tasks to hide the boss bar
    }

    private BossBar handleNewBossBar(Player player, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed) {
        Locale locale = plugin.getUser(player).getLocale();
        BossBar.Color color = getColor(skill);
        BossBar.Overlay overlay = getOverlay(skill);
        String bossBarText = getBossBarText(player, skill, currentXp, (long) levelXp, (long) xpGained, level, maxed, locale);

        Component name = LegacyComponentSerializer.legacySection().deserialize(bossBarText);

        // Calculate xp progress
        double progress = currentXp / levelXp;
        if (progress > 1 || progress < 0) {
            progress = 1.0;
        }
        BossBar bossBar = BossBar.bossBar(name, (float) progress, color, overlay);

        plugin.getAudiences().player(player).showBossBar(bossBar);
        // Add to maps
        if (mode.equals("single")) {
            singleBossBars.put(player.getUniqueId(), bossBar);
        } else {
            bossBars.get(player.getUniqueId()).put(skill, bossBar);
        }
        return bossBar;
    }

    private void handleExistingBossBar(BossBar bossBar, Player player, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed) {
        Locale locale = plugin.getUser(player).getLocale();
        String bossBarText = getBossBarText(player, skill, currentXp, (long) levelXp, xpGained, level, maxed, locale);

        Component name = LegacyComponentSerializer.legacySection().deserialize(bossBarText);

        bossBar.name(name); // Update the boss bar to the new text value
        // Calculate xp progress
        double progress = currentXp / levelXp;
        if (progress > 1 || progress < 0) {
            progress = 1.0;
        }
        bossBar.progress((float) progress);

        plugin.getAudiences().player(player).showBossBar(bossBar);
    }

    private String getBossBarText(Player player, Skill skill, double currentXp, long levelXp, double xpGained, int level, boolean maxed, Locale locale) {
        String bossBarText;
        String currentXpText = getCurrentXpText(currentXp);
        if (!maxed) {
            bossBarText = setPlaceholders(player, TextUtil.replace(plugin.getMsg(ActionBarMessage.BOSS_BAR_XP, locale),
                    "{skill}", skill.getDisplayName(locale),
                    "{level}", RomanNumber.toRoman(level, plugin),
                    "{current_xp}", currentXpText,
                    "{level_xp}", getLevelXpText(levelXp),
                    "{percent}", NumberUtil.format2(currentXp / (double) levelXp * 100),
                    "{xp_gained}", NumberUtil.format1(xpGained)));
        } else {
            bossBarText = setPlaceholders(player, TextUtil.replace(plugin.getMsg(ActionBarMessage.BOSS_BAR_MAXED, locale),
                    "{skill}", skill.getDisplayName(locale),
                    "{level}", RomanNumber.toRoman(level, plugin),
                    "{current_xp}", currentXpText,
                    "{xp_gained}", NumberUtil.format1(xpGained)));
        }
        return bossBarText;
    }

    private String getLevelXpText(long levelXp) {
        if (plugin.configBoolean(Option.BOSS_BAR_USE_SUFFIX)) {
            return BigNumber.withSuffix(levelXp);
        } else {
            return String.valueOf(levelXp);
        }
    }

    // Get the formatted text for the current player xp depending on rounding option
    private String getCurrentXpText(double currentXp) {
        String currentXpText;
        if (plugin.configBoolean(Option.BOSS_BAR_ROUND_XP)) {
            currentXpText = String.valueOf(Math.round(currentXp));
        } else {
            currentXpText = nf.format(currentXp);
        }
        return currentXpText;
    }

    public void incrementAction(Player player, Skill skill) {
        UUID playerId = player.getUniqueId();
        if (!singleCheckCurrentActions.containsKey(playerId)) singleCheckCurrentActions.put(playerId, 0);
        if (!checkCurrentActions.containsKey(playerId)) checkCurrentActions.put(playerId, new HashMap<>());
        // Increment current action
        if (mode.equals("single")) {
            singleCheckCurrentActions.put(playerId, singleCheckCurrentActions.get(playerId) + 1);
        }
        else {
            Integer currentAction = checkCurrentActions.get(playerId).get(skill);
            if (currentAction != null) {
                checkCurrentActions.get(playerId).put(skill, currentAction + 1);
            } else {
                checkCurrentActions.get(playerId).put(skill, 0);
            }
        }
    }

    private void scheduleHide(UUID playerId, Skill skill, BossBar bossBar) {
        if (mode.equals("single")) {
            final int currentAction = singleCurrentActions.get(playerId);
            plugin.getScheduler().scheduleSync(() -> {
                if (!mode.equals("single")) {
                    return;
                }
                if (currentAction != singleCurrentActions.getOrDefault(playerId, 0)) {
                    return;
                }
                if (bossBar != null) {
                    plugin.getAudiences().player(playerId).hideBossBar(bossBar);
                }
                singleCheckCurrentActions.remove(playerId);
            }, stayTime * 50L, TimeUnit.MILLISECONDS);
        } else {
            Map<Skill, Integer> multiCurrentActions = currentActions.get(playerId);
            if (multiCurrentActions == null) {
                return;
            }
            final int currentAction = multiCurrentActions.get(skill);

            plugin.getScheduler().scheduleSync(() -> {
                if (mode.equals("single")) {
                    return;
                }
                Map<Skill, Integer> currActions = currentActions.get(playerId);
                if (currActions == null) {
                    return;
                }
                if (currentAction != currActions.getOrDefault(skill, 0)) {
                    return;
                }
                if (bossBar != null) {
                    plugin.getAudiences().player(playerId).hideBossBar(bossBar);
                }
                checkCurrentActions.remove(playerId);
            }, stayTime * 50L, TimeUnit.MILLISECONDS);
        }
    }

    private BossBar.Color getColor(Skill skill) {
        return colors.getOrDefault(skill, BossBar.Color.GREEN);
    }

    private BossBar.Overlay getOverlay(Skill skill) {
        return overlays.getOrDefault(skill, BossBar.Overlay.PROGRESS);
    }

    public int getCurrentAction(Player player, Skill skill) {
        if (mode.equals("single")) {
            return singleCheckCurrentActions.get(player.getUniqueId());
        } else {
            Map<Skill, Integer> multiCurrentActions = checkCurrentActions.get(player.getUniqueId());
            if (multiCurrentActions != null) {
                return multiCurrentActions.get(skill);
            }
        }
        return -1;
    }

    private String setPlaceholders(Player player, String input) {
        if (plugin.configBoolean(Option.BOSS_BAR_PLACEHOLDER_API) && plugin.getHookManager().isRegistered(PlaceholderHook.class)) {
            return plugin.getHookManager().getHook(PlaceholderHook.class).setPlaceholders(plugin.getUser(player), input);
        } else {
            return input;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        bossBars.remove(playerId);
        currentActions.remove(playerId);
        singleBossBars.remove(playerId);
        singleCurrentActions.remove(playerId);
        checkCurrentActions.remove(playerId);
        singleCheckCurrentActions.remove(playerId);
    }
    
}
