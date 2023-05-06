package dev.auramc.auraskills.common.rewards.type;


import dev.auramc.auraskills.api.skill.Skill;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.hooks.PermissionsHook;

public class PermissionReward extends MessagedReward {

    private final String permission;
    private final boolean value;

    public PermissionReward(AuraSkillsPlugin plugin, String menuMessage, String chatMessage, String permission, boolean value) {
        super(plugin, menuMessage, chatMessage);
        this.permission = permission;
        this.value = value;
    }

    @Override
    public void giveReward(PlayerData playerData, Skill skill, int level) {
        if (hooks.isRegistered(PermissionsHook.class)) {
            hooks.getHook(PermissionsHook.class).setPermission(playerData, permission, value);
        }
    }

    public String getPermission() {
        return permission;
    }

    public boolean getValue() {
        return value;
    }

}
