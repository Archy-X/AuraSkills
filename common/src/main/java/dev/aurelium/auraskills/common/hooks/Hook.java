package dev.aurelium.auraskills.common.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class Hook {

    protected final AuraSkillsPlugin plugin;
    private final ConfigurationNode config;

    public Hook(AuraSkillsPlugin plugin, ConfigurationNode config) {
        this.plugin = plugin;
        this.config = config;
    }

    public ConfigurationNode getConfig() {
        return config;
    }

    public abstract Class<? extends Hook> getTypeClass();

}
