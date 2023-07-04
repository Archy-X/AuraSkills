package dev.aurelium.auraskills.common.stat;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.player.User;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface with methods to manage player stats.
 */
public abstract class StatManager {

    private final AuraSkillsPlugin plugin;
    private final Map<Stat, LoadedStat> statMap;
    private final StatSupplier supplier;

    public StatManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.statMap = new HashMap<>();
        this.supplier = new StatSupplier(this, plugin.getMessageProvider());
    }

    public StatSupplier getSupplier() {
        return supplier;
    }

    @NotNull
    public LoadedStat getStat(Stat stat) {
        LoadedStat loadedStat = statMap.get(stat);
        if (loadedStat == null) {
            throw new IllegalArgumentException("Stat " + stat + " is not loaded!");
        }
        return loadedStat;
    }

    public void register(Stat stat, LoadedStat loadedStat) {
        statMap.put(stat, loadedStat);
    }

    public abstract void reloadPlayer(User user);

    public abstract <T> void reload(User user, T type);

    public abstract void reloadStat(User user, Stat stat);

    public void updateStats(User user) {
        if (user == null) return;
        for (Stat stat : plugin.getStatRegistry().getValues()) {
            user.setStatLevel(stat, 0);
        }
        for (Skill skill : plugin.getSkillRegistry().getValues()) {
            plugin.getRewardManager().getRewardTable(skill).applyStats(user, user.getSkillLevel(skill));
        }
        // Reloads modifiers
        for (String key : user.getStatModifiers().keySet()) {
            StatModifier modifier = user.getStatModifiers().get(key);
            user.addStatLevel(modifier.stat(), modifier.value());
        }
        reloadStat(user, Stats.HEALTH);
        reloadStat(user, Stats.WISDOM);
    }

}
