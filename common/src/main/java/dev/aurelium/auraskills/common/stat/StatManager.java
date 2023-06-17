package dev.aurelium.auraskills.common.stat;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.StatProvider;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.data.PlayerData;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Interface with methods to manage player stats.
 */
public abstract class StatManager implements StatProvider {

    private final AuraSkillsPlugin plugin;
    private final Map<Stat, LoadedStat> statMap;

    public StatManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.statMap = new HashMap<>();
    }

    public void register(Stat stat, LoadedStat loadedStat) {
        statMap.put(stat, loadedStat);
    }

    @Override
    public String getDisplayName(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatDisplayName(stat, locale);
    }

    @Override
    public String getDescription(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatDescription(stat, locale);
    }

    @Override
    public String getColor(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatColor(stat, locale);
    }

    @Override
    public String getSymbol(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatSymbol(stat, locale);
    }

    public abstract void reloadStat(PlayerData playerData, Stat stat);

    public void updateStats(PlayerData playerData) {
        if (playerData == null) return;
        for (Stat stat : plugin.getStatRegistry().getValues()) {
            playerData.setStatLevel(stat, 0);
        }
        for (Skill skill : plugin.getSkillRegistry().getValues()) {
            plugin.getRewardManager().getRewardTable(skill).applyStats(playerData, playerData.getSkillLevel(skill));
        }
        // Reloads modifiers
        for (String key : playerData.getStatModifiers().keySet()) {
            StatModifier modifier = playerData.getStatModifiers().get(key);
            playerData.addStatLevel(modifier.stat(), modifier.value());
        }
        reloadStat(playerData, Stats.HEALTH);
        reloadStat(playerData, Stats.WISDOM);
    }

}
