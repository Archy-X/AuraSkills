package dev.aurelium.auraskills.common.user;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public record UserState(UUID uuid, Map<Skill, Integer> skillLevels, Map<Skill, Double> skillXp,
        Map<String, StatModifier> statModifiers, Map<String, TraitModifier> traitModifiers, double mana) {

    public UserState withUuid(UUID newUuid) {
        return new UserState(newUuid, skillLevels, skillXp, statModifiers, traitModifiers, mana);
    }

    public static UserState createEmpty(UUID uuid, AuraSkillsPlugin plugin) {
        // Fill maps with registered skills and default levels
        Map<Skill, Integer> levels = new ConcurrentHashMap<>();
        Map<Skill, Double> xp = new ConcurrentHashMap<>();
        for (Skill skill : plugin.getSkillRegistry().getValues()) {
            levels.put(skill, plugin.config().getStartLevel());
            xp.put(skill, 0.0);
        }
        return new UserState(uuid, levels, xp, Map.of(), Map.of(), 0.0);
    }

}
