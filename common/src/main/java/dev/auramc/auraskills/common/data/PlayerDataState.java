package dev.auramc.auraskills.common.data;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.api.stat.StatModifier;
import dev.auramc.auraskills.common.AuraSkillsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record PlayerDataState(UUID uuid, Map<Skill, Integer> skillLevels, Map<Skill, Double> skillXp,
                              Map<String, StatModifier> statModifiers, double mana) {

    public static PlayerDataState createEmpty(UUID uuid, AuraSkillsPlugin plugin) {
        // Fill maps with registered skills and default levels
        Map<Skill, Integer> levels = new HashMap<>();
        Map<Skill, Double> xp = new HashMap<>();
        for (Skill skill : plugin.getSkillRegistry().getValues()) {
            levels.put(skill, plugin.getConfigProvider().getStartLevel());
            xp.put(skill, 0.0);
        }
        return new PlayerDataState(uuid, levels, xp, new HashMap<>(), 0.0);
    }

}
