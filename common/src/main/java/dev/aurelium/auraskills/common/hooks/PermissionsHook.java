package dev.aurelium.auraskills.common.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class PermissionsHook extends Hook {

    public PermissionsHook(AuraSkillsPlugin plugin, ConfigurationNode config) {
        super(plugin, config);
    }

    public abstract void setPermission(User user, String permission, boolean value);

    public abstract void unsetPermission(User user, String permission, boolean value);

}
