package dev.aurelium.skills.common.hooks;

import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.data.PlayerData;

public abstract class PermissionsHook extends Hook {

    public PermissionsHook(AureliumSkillsPlugin plugin) {
        super(plugin);
    }

    public abstract void setPermission(PlayerData playerData, String permission, boolean value);

    public abstract void unsetPermission(PlayerData playerData, String permission, boolean value);

}
