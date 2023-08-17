package dev.aurelium.auraskills.common.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.user.User;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class PlaceholderHook extends Hook {

    public PlaceholderHook(AuraSkillsPlugin plugin, ConfigurationNode config) {
        super(plugin, config);
    }

    public abstract String setPlaceholders(User user, String message);

}
