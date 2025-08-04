package dev.aurelium.auraskills.bukkit.requirement;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public class WorldNode extends RequirementNode {

    private final String world;

    public WorldNode(AuraSkills plugin, String world, String message) {
        super(plugin, message);
        this.world = world;
    }

    @Override
    public boolean check(Player player) {
        if (!player.getWorld().getName().equalsIgnoreCase(world)) {
            return false;
        }

        return true;
    }

}
