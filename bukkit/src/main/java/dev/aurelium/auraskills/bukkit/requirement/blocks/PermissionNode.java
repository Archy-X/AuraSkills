package dev.aurelium.auraskills.bukkit.requirement.blocks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public class PermissionNode extends RequirementNode {

    private final String permission;

    public PermissionNode(AuraSkills plugin, String permission, String message) {
        super(plugin, message);
        this.permission = permission;
    }

    @Override
    public boolean check(Player player) {
        return player.hasPermission(permission);
    }

}
