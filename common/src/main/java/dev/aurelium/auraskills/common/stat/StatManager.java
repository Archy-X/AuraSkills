package dev.aurelium.auraskills.common.stat;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Interface with methods to manage player stats.
 */
public abstract class StatManager {

    protected final AuraSkillsPlugin plugin;
    private final Map<Stat, LoadedStat> statMap;
    private final StatSupplier supplier;

    public StatManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.statMap = new LinkedHashMap<>();
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
    
    public Collection<LoadedStat> getStats() {
        return statMap.values();
    }
    
    public Set<Stat> getEnabledStats() {
        Set<Stat> stats = new LinkedHashSet<>();
        for (LoadedStat loaded : statMap.values()) {
            if (!loaded.options().enabled()) continue;
            stats.add(loaded.stat());
        }
        return stats;
    }

    public void register(Stat stat, LoadedStat loadedStat) {
        statMap.put(stat, loadedStat);
    }

    public void unregisterAll() {
        statMap.clear();
    }

    public abstract void reloadPlayer(User user);

    public abstract <T> void reload(User user, T type);

    public abstract void reloadStat(User user, Stat stat);

    public void updateStats(User user) {
        if (user == null) return;
        for (Stat stat : plugin.getStatRegistry().getValues()) {
            user.setStatLevel(stat, 0);
        }
        for (Skill skill : plugin.getSkillManager().getSkillValues()) {
            if (!user.hasSkillPermission(skill)) continue;
            plugin.getRewardManager().getRewardTable(skill).applyStats(user, user.getSkillLevel(skill));
        }
        // Reloads modifiers
        for (String key : user.getStatModifiers().keySet()) {
            StatModifier modifier = user.getStatModifiers().get(key);
            user.addStatLevel(modifier.stat(), modifier.value());
        }
        reloadStats(user);
    }

    private void reloadStats(User user) {
        for (Stat stat : getEnabledStats()) {
            reloadStat(user, stat);
        }
    }

    public boolean isLoaded(Stat stat) {
        return statMap.containsKey(stat);
    }

}
