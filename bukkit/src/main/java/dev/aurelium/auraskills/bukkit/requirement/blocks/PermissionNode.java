package dev.aurelium.auraskills.bukkit.requirement.blocks;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public class PermissionNode extends RequirementNode {

    private final String permission;
    private final String message;

    public PermissionNode(AuraSkills plugin, String permission, String message) {
        super(plugin);
        this.permission = permission;
        this.message = message;
    }

    public boolean check(Player player) {
        return player.hasPermission(permission);
    }

    public String getDenyMessage() {
        return message;
    }
}
