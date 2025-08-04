package dev.aurelium.auraskills.bukkit.requirement.blocks;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public class SkillNode extends RequirementNode {

    private final Skill skill;
    private final int level;

    public SkillNode(AuraSkills plugin, Skill skill, int level, String message) {
        super(plugin, message);
        this.skill = skill;
        this.level = level;
    }

    @Override
    public boolean check(Player player) {
        return plugin.getUser(player).getSkillLevel(skill) >= level;
    }

}
