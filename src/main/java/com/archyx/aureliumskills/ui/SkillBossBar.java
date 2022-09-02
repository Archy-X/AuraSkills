package com.archyx.aureliumskills.ui;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.lang.ActionBarMessage;
import com.archyx.aureliumskills.lang.Lang;
import com.archyx.aureliumskills.skills.Skill;
import com.archyx.aureliumskills.skills.Skills;
import com.archyx.aureliumskills.util.math.BigNumber;
import com.archyx.aureliumskills.util.math.RomanNumber;
import com.archyx.aureliumskills.util.text.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SkillBossBar implements Listener {

    private final Map<Player, Map<Skill, BossBar>> bossBars;
    private final Map<Player, Map<Skill, Integer>> currentActions;
    private final Map<Player, Map<Skill, Integer>> checkCurrentActions;
    private final Map<Player, BossBar> singleBossBars;
    private final Map<Player, Integer> singleCurrentActions;
    private final Map<Player, Integer> singleCheckCurrentActions;
    private String mode;
    private int stayTime;
    private Map<Skill, BarColor> colors;
    private Map<Skill, BarStyle> styles;
    private final NumberFormat nf = new DecimalFormat("#.#");
    private final @NotNull AureliumSkills plugin;

    public SkillBossBar(@NotNull AureliumSkills plugin) {
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
        for (Map.Entry<Player, BossBar> entry : singleBossBars.entrySet()) {
            entry.getValue().setVisible(false);
            entry.getValue().removeAll();
        }
        for (Map.Entry<Player, Map<Skill, BossBar>> entry : bossBars.entrySet()) {
            Map<Skill, BossBar> bossBars = entry.getValue();
            for (Map.Entry<Skill, BossBar> bossBarEntry : bossBars.entrySet()) {
                bossBarEntry.getValue().setVisible(false);
                bossBarEntry.getValue().removeAll();
            }
        }
        bossBars.clear();
        singleBossBars.clear();
    }

    public void sendBossBar(@NotNull Player player, Skill skill, double currentXp, double levelXp, int level, boolean maxed) {
        if (maxed && !OptionL.getBoolean(Option.BOSS_BAR_DISPLAY_MAXED)) { // display-maxed option
            return;
        }
        Locale locale = plugin.getLang().getLocale(player);
        BarColor color = getColor(skill);
        BarStyle style = getStyle(skill);
        BossBar bossBar;
        // Single Mode
        if (mode.equals("single")) {
            bossBar = singleBossBars.get(player);
        }
        else {
            if (!bossBars.containsKey(player)) bossBars.put(player, new HashMap<>());
            bossBar = bossBars.get(player).get(skill);
        }
        // If player does not have a boss bar in that skill
        if (bossBar == null) {
            if (!maxed) {
                if (!OptionL.getBoolean(Option.BOSS_BAR_ROUND_XP)) {
                    bossBar = Bukkit.createBossBar(TextUtil.replace(Lang.getMessage(ActionBarMessage.BOSS_BAR_XP, locale),
                            "{skill}", skill.getDisplayName(locale),
                            "{level}", RomanNumber.toRoman(level),
                            "{current_xp}", nf.format(currentXp),
                            "{level_xp}", BigNumber.withSuffix((long) levelXp)), color, style);
                }
                else {
                    bossBar = Bukkit.createBossBar(TextUtil.replace(Lang.getMessage(ActionBarMessage.BOSS_BAR_XP, locale),
                            "{skill}", skill.getDisplayName(locale),
                            "{level}", RomanNumber.toRoman(level),
                            "{current_xp}", String.valueOf((int) currentXp),
                            "{level_xp}", BigNumber.withSuffix((long) levelXp)), color, style);
                }
            }
            else {
                bossBar = Bukkit.createBossBar(TextUtil.replace(Lang.getMessage(ActionBarMessage.BOSS_BAR_MAXED, locale),
                        "{skill}", skill.getDisplayName(locale),
                        "{level}", RomanNumber.toRoman(level)), color, style);
            }
            double progress = currentXp / levelXp;
            if (progress <= 1 && progress >= 0) {
                bossBar.setProgress(currentXp / levelXp);
            }
            else {
                bossBar.setProgress(1.0);
            }
            bossBar.addPlayer(player);
            // Add to maps
            if (mode.equals("single")) {
                singleBossBars.put(player, bossBar);
            }
            else {
                bossBars.get(player).put(skill, bossBar);
            }
        }
        // Use existing one
        else {
            if (!maxed) {
                if (!OptionL.getBoolean(Option.BOSS_BAR_ROUND_XP)) {
                    bossBar.setTitle(TextUtil.replace(Lang.getMessage(ActionBarMessage.BOSS_BAR_XP, locale),
                            "{skill}", skill.getDisplayName(locale),
                            "{level}", RomanNumber.toRoman(level),
                            "{current_xp}", nf.format(currentXp),
                            "{level_xp}", BigNumber.withSuffix((long) levelXp)));
                }
                else {
                    bossBar.setTitle(TextUtil.replace(Lang.getMessage(ActionBarMessage.BOSS_BAR_XP, locale),
                            "{skill}", skill.getDisplayName(locale),
                            "{level}", RomanNumber.toRoman(level),
                            "{current_xp}", String.valueOf((int) currentXp),
                            "{level_xp}", BigNumber.withSuffix((long) levelXp)));
                }
            }
            else {
                bossBar.setTitle(TextUtil.replace(Lang.getMessage(ActionBarMessage.BOSS_BAR_MAXED, locale),
                        "{level}", RomanNumber.toRoman(level),
                        "{skill}", skill.getDisplayName(locale)));
            }
            double progress = currentXp / levelXp;
            if (progress <= 1 && progress >= 0) {
                bossBar.setProgress(currentXp / levelXp);
            }
            else {
                bossBar.setProgress(1.0);
            }
            bossBar.setVisible(true);
        }
        // Increment current action
        if (mode.equals("single")) {
            Integer currentAction = singleCurrentActions.get(player);
            if (currentAction != null) {
                singleCurrentActions.put(player, currentAction + 1);
            }
            else {
                singleCurrentActions.put(player, 0);
            }
        }
        else {
            if (!currentActions.containsKey(player)) currentActions.put(player, new HashMap<>());
            Integer currentAction = currentActions.get(player).get(skill);
            if (currentAction != null) {
                currentActions.get(player).put(skill, currentAction + 1);
            } else {
                currentActions.get(player).put(skill, 0);
            }
        }
        scheduleHide(player, skill, bossBar);
    }

    public void incrementAction(Player player, Skill skill) {
        if (!singleCheckCurrentActions.containsKey(player)) singleCheckCurrentActions.put(player, 0);
        if (!checkCurrentActions.containsKey(player)) checkCurrentActions.put(player, new HashMap<>());
        // Increment current action
        if (mode.equals("single")) {
            singleCheckCurrentActions.put(player, singleCheckCurrentActions.get(player) + 1);
        }
        else {
            Integer currentAction = checkCurrentActions.get(player).get(skill);
            if (currentAction != null) {
                checkCurrentActions.get(player).put(skill, currentAction + 1);
            } else {
                checkCurrentActions.get(player).put(skill, 0);
            }
        }
    }

    private void scheduleHide(Player player, Skill skill, BossBar bossBar) {
        if (mode.equals("single")) {
            final int currentAction = singleCurrentActions.get(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (mode.equals("single")) {
                        if (currentAction == singleCurrentActions.get(player)) {
                            bossBar.setVisible(false);
                            singleCheckCurrentActions.remove(player);
                        }
                    }
                }
            }.runTaskLater(plugin, stayTime);
        }
        else {
            Map<Skill, Integer> multiCurrentActions = currentActions.get(player);
            if (multiCurrentActions != null) {
                final int currentAction = multiCurrentActions.get(skill);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!mode.equals("single")) {
                            Map<Skill, Integer> multiCurrentActions = currentActions.get(player);
                            if (multiCurrentActions != null) {
                                if (currentAction == multiCurrentActions.get(skill)) {
                                    bossBar.setVisible(false);
                                    checkCurrentActions.remove(player);
                                }
                            }
                        }
                    }
                }.runTaskLater(plugin, stayTime);
            }
        }
    }

    private @NotNull BarColor getColor(Skill skill) {
        BarColor color = colors.get(skill);
        if (color == null) color = BarColor.GREEN;
        return color;
    }

    private @NotNull BarStyle getStyle(Skill skill) {
        BarStyle style = styles.get(skill);
        if (style == null) style = BarStyle.SOLID;
        return style;
    }

    public int getCurrentAction(Player player, Skill skill) {
        if (mode.equals("single")) {
            return singleCheckCurrentActions.get(player);
        }
        else {
            Map<Skill, Integer> multiCurrentActions = checkCurrentActions.get(player);
            if (multiCurrentActions != null) {
                return multiCurrentActions.get(skill);
            }
        }
        return -1;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        bossBars.remove(player);
        currentActions.remove(player);
        singleBossBars.remove(player);
        singleCurrentActions.remove(player);
        checkCurrentActions.remove(player);
        singleCheckCurrentActions.remove(player);
    }

}
