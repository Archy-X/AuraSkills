package dev.aurelium.auraskills.bukkit.requirement.blocks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public abstract class RequirementNode {

    protected AuraSkills plugin;

    public RequirementNode(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public abstract String getDenyMessage();
    public abstract boolean check(Player player);
}
