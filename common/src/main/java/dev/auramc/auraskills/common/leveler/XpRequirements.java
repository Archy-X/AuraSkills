package dev.auramc.auraskills.common.leveler;

import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.AuraSkillsPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager for storing and retrieving xp requirements for each level.
 * Does not handle loading requirements from file.
 */
public class XpRequirements {

    private final AuraSkillsPlugin plugin;
    private final List<Integer> defaultXpRequirements;
    private final Map<Skill, List<Integer>> skillXpRequirements;

    public XpRequirements(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
        this.defaultXpRequirements = new ArrayList<>();
        this.skillXpRequirements = new HashMap<>();
        addDefaultXpRequirements();
    }

    public void addSkillXpRequirements(Skill skill, List<Integer> xpRequirements) {
        skillXpRequirements.put(skill, xpRequirements);
    }

    public int getXpRequired(Skill skill, int level) {
        // Use skill specific xp requirements if exists
        List<Integer> skillList = skillXpRequirements.get(skill);
        if (skillList != null) {
            if (skillList.size() > level - 2) {
                return skillList.get(level - 2);
            } else {
                return 0;
            }
        }
        // Else use default
        return getDefaultXpRequired(level);
    }

    public int getDefaultXpRequired(int level) {
        if (defaultXpRequirements.size() > level - 2) {
            return defaultXpRequirements.get(level - 2);
        } else {
            return 0;
        }
    }

    public int getListSize(Skill skill) {
        List<Integer> skillList = skillXpRequirements.get(skill);
        if (skillList != null) {
            return skillList.size();
        }
        return defaultXpRequirements.size();
    }

    private void addDefaultXpRequirements() {
        defaultXpRequirements.clear();
        int highestMaxLevel = plugin.config().getHighestMaxLevel();
        for (int i = 0; i < highestMaxLevel - 1; i++) {
            defaultXpRequirements.add(100 * i * i + 100);
        }
    }

}
