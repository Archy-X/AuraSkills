package dev.aurelium.auraskills.bukkit.requirement.blocks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public class ExcludedWorldNode extends RequirementNode {

    private final String[] worlds;

    public ExcludedWorldNode(AuraSkills plugin, String[] worlds, String message) {
        super(plugin, message);
        this.worlds = worlds;
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

}
