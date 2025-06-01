package dev.aurelium.auraskills.bukkit.requirement.blocks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public abstract class RequirementNode {

    protected AuraSkills plugin;
    protected String denyMessage;

    public RequirementNode(AuraSkills plugin, String denyMessage) {
        this.plugin = plugin;
        this.denyMessage = denyMessage;
    }

    public abstract boolean check(Player player);

    public String getDenyMessage() {
        return denyMessage;
    }

}
