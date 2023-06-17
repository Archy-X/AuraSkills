package dev.aurelium.auraskills.common.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.data.PlayerData;

public abstract class PermissionsHook extends Hook {

    public PermissionsHook(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    public abstract void setPermission(PlayerData playerData, String permission, boolean value);

    public abstract void unsetPermission(PlayerData playerData, String permission, boolean value);

}
