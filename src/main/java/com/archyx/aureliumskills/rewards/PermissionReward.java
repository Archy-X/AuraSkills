package com.archyx.aureliumskills.rewards;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PermissionReward extends MessagedReward {

    private final @NotNull String permission;
    private final boolean value;

    public PermissionReward(@NotNull AureliumSkills plugin, @NotNull String menuMessage, @NotNull String chatMessage, @NotNull String permission, boolean value) {
        super(plugin, menuMessage, chatMessage);
        this.permission = permission;
        this.value = value;
    }

    @Override
    public void giveReward(@NotNull Player player, @NotNull Skill skill, int level) {
        if (plugin.isLuckPermsEnabled()) {
            plugin.getLuckPermsSupport().addPermission(player, permission, value);
        }
    }

    public @NotNull String getPermission() {
        return permission;
    }

    public boolean getValue() {
        return value;
    }

}
