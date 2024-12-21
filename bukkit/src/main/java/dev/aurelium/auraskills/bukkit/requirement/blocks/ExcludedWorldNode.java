package dev.aurelium.auraskills.bukkit.requirement.blocks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public class ExcludedWorldNode extends RequirementNode {

    private final String[] worlds;
    private final String message;

    public ExcludedWorldNode(AuraSkills plugin, String[] worlds, String message) {
        super(plugin);
        this.worlds = worlds;
        this.message = message;
    }

    @Override
    public boolean check(Player player) {
        for (String world : worlds) {
            if (player.getWorld().getName().equalsIgnoreCase(world)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getDenyMessage() {
        return message;
    }
}
