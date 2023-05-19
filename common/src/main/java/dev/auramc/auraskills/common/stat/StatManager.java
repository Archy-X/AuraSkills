package dev.auramc.auraskills.common.stat;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.stat.Stat;
import dev.auramc.auraskills.api.stat.StatModifier;
import dev.auramc.auraskills.api.stat.Stats;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;

/**
 * Interface with methods to manage player stats.
 */
public abstract class StatManager {

    private final AuraSkillsPlugin plugin;

    public StatManager(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
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
