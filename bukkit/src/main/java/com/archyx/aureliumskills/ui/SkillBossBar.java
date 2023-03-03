package com.archyx.aureliumskills.ui;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.ActionBarMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.util.math.BigNumber;
import com.archyx.aureliumskills.util.math.NumberUtil;
import com.archyx.aureliumskills.util.math.RomanNumber;
import com.archyx.aureliumskills.util.text.TextUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class SkillBossBar implements Listener {

    private final Map<UUID, Map<Skill, BossBar>> bossBars;
    private final Map<UUID, Map<Skill, Integer>> currentActions;
    private final Map<UUID, Map<Skill, Integer>> checkCurrentActions;
    private final Map<UUID, BossBar> singleBossBars;
    private final Map<UUID, Integer> singleCurrentActions;
    private final Map<UUID, Integer> singleCheckCurrentActions;
    private String mode;
    private int stayTime;
    private Map<Skill, BarColor> colors;
    private Map<Skill, BarStyle> styles;
    private final NumberFormat nf = new DecimalFormat("#.#");
    private final AureliumSkills plugin;

    public SkillBossBar(AureliumSkills plugin) {
        this.bossBars = new HashMap<>();
        this.currentActions = new HashMap<>();
        this.plugin = plugin;
        this.singleBossBars = new HashMap<>();
        this.singleCurrentActions = new HashMap<>();
        this.checkCurrentActions = new HashMap<>();
        this.singleCheckCurrentActions = new HashMap<>();
    }

    public void loadOptions() {
        mode = OptionL.getString(Option.BOSS_BAR_MODE);
        stayTime = OptionL.getInt(Option.BOSS_BAR_STAY_TIME);
        colors = new HashMap<>();
        styles = new HashMap<>();
        for (String entry : OptionL.getList(Option.BOSS_BAR_FORMAT)) {
            String[] splitEntry = entry.split(" ");
            Skill skill;
            BarColor color = BarColor.GREEN;
            BarStyle style = BarStyle.SOLID;
            skill = plugin.getSkillRegistry().getSkill(splitEntry[0].toUpperCase());
            if (skill == null) {
                plugin.getLogger().warning("Error loading boss bar format in config.yml: " + splitEntry[0] + " is not a valid Skill");
                skill = Skills.FARMING;
            }
            if (splitEntry.length > 1) {
                try {
                    color = BarColor.valueOf(splitEntry[1].toUpperCase());
                }
                catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Error loading boss bar format in config.yml: " + splitEntry[0] + " is not a valid BarColor");
                }
                if (splitEntry.length > 2) {
                    try {
                        style = BarStyle.valueOf(splitEntry[2].toUpperCase());
                    }
                    catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Error loading boss bar format in config.yml: " + splitEntry[0] + " is not a valid BarStyle");
                    }
                }
            }
            colors.put(skill, color);
            styles.put(skill, style);
        }
        for (Map.Entry<UUID, BossBar> entry : singleBossBars.entrySet()) {
            entry.getValue().setVisible(false);
            entry.getValue().removeAll();
        }
        for (Map.Entry<UUID, Map<Skill, BossBar>> entry : bossBars.entrySet()) {
            Map<Skill, BossBar> bossBars = entry.getValue();
            for (Map.Entry<Skill, BossBar> bossBarEntry : bossBars.entrySet()) {
                bossBarEntry.getValue().setVisible(false);
                bossBarEntry.getValue().removeAll();
            }
        }
        bossBars.clear();
        singleBossBars.clear();
    }

    public void sendBossBar(Player player, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed) {
        UUID playerId = player.getUniqueId();
        if (maxed && !OptionL.getBoolean(Option.BOSS_BAR_DISPLAY_MAXED)) { // display-maxed option
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
        Locale locale = plugin.getLang().getLocale(player);
        BarColor color = getColor(skill);
        BarStyle style = getStyle(skill);
        String bossBarText = getBossBarText(player, skill, currentXp, (long) levelXp, (long) xpGained, level, maxed, locale);

        BossBar bossBar = Bukkit.createBossBar(bossBarText, color, style);
        // Calculate xp progress
        double progress = currentXp / levelXp;
        if (progress <= 1 && progress >= 0) {
            bossBar.setProgress(currentXp / levelXp);
        } else {
            bossBar.setProgress(1.0);
        }
        bossBar.addPlayer(player);
        // Add to maps
        if (mode.equals("single")) {
            singleBossBars.put(player.getUniqueId(), bossBar);
        } else {
            bossBars.get(player.getUniqueId()).put(skill, bossBar);
        }
        return bossBar;
    }

    private void handleExistingBossBar(BossBar bossBar, Player player, Skill skill, double currentXp, double levelXp, double xpGained, int level, boolean maxed) {
        Locale locale = plugin.getLang().getLocale(player);
        String bossBarText = getBossBarText(player, skill, currentXp, (long) levelXp, xpGained, level, maxed, locale);

        bossBar.setTitle(bossBarText); // Update the boss bar to the new text value
        // Calculate xp progress
        double progress = currentXp / levelXp;
        if (progress <= 1 && progress >= 0) {
            bossBar.setProgress(currentXp / levelXp);
        } else {
            bossBar.setProgress(1.0);
        }
        bossBar.setVisible(true); // Show the boss bar to the player
    }

    private String getBossBarText(Player player, Skill skill, double currentXp, long levelXp, double xpGained, int level, boolean maxed, Locale locale) {
        String bossBarText;
        String currentXpText = getCurrentXpText(currentXp);
        if (!maxed) {
            bossBarText = setPlaceholders(player, TextUtil.replace(Lang.getMessage(ActionBarMessage.BOSS_BAR_XP, locale),
                    "{skill}", skill.getDisplayName(locale),
                    "{level}", RomanNumber.toRoman(level),
                    "{current_xp}", currentXpText,
                    "{level_xp}", getLevelXpText(levelXp),
                    "{xp_gained}", NumberUtil.format1(xpGained)));
        } else {
            bossBarText = setPlaceholders(player, TextUtil.replace(Lang.getMessage(ActionBarMessage.BOSS_BAR_MAXED, locale),
                    "{skill}", skill.getDisplayName(locale),
                    "{level}", RomanNumber.toRoman(level),
                    "{current_xp}", currentXpText,
                    "{xp_gained}", NumberUtil.format1(xpGained)));
        }
        return bossBarText;
    }

    private String getLevelXpText(long levelXp) {
        if (OptionL.getBoolean(Option.BOSS_BAR_USE_SUFFIX)) {
            return BigNumber.withSuffix(levelXp);
        } else {
            return String.valueOf(levelXp);
        }
    }

    // Get the formatted text for the current player xp depending on rounding option
    private String getCurrentXpText(double currentXp) {
        String currentXpText;
        if (OptionL.getBoolean(Option.BOSS_BAR_ROUND_XP)) {
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
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (mode.equals("single")) {
                        if (currentAction == singleCurrentActions.getOrDefault(playerId, 0)) {
                            if (bossBar != null) {
                                bossBar.setVisible(false);
                            }
                            singleCheckCurrentActions.remove(playerId);
                        }
                    }
                }
            }.runTaskLater(plugin, stayTime);
        }
        else {
            Map<Skill, Integer> multiCurrentActions = currentActions.get(playerId);
            if (multiCurrentActions != null) {
                final int currentAction = multiCurrentActions.get(skill);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!mode.equals("single")) {
                            Map<Skill, Integer> multiCurrentActions = currentActions.get(playerId);
                            if (multiCurrentActions != null) {
                                if (currentAction == multiCurrentActions.getOrDefault(skill, 0)) {
                                    if (bossBar != null) {
                                        bossBar.setVisible(false);
                                    }
                                    checkCurrentActions.remove(playerId);
                                }
                            }
                        }
                    }
                }.runTaskLater(plugin, stayTime);
            }
        }
    }

    private BarColor getColor(Skill skill) {
        return colors.getOrDefault(skill, BarColor.GREEN);
    }

    private BarStyle getStyle(Skill skill) {
        return styles.getOrDefault(skill, BarStyle.SOLID);
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
        if (OptionL.getBoolean(Option.BOSS_BAR_PLACEHOLDER_API) && plugin.isPlaceholderAPIEnabled()) {
            return PlaceholderAPI.setPlaceholders(player, input);
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
