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

    /**
     * Called when the plugin is disabled, allowing the hook to unregister anything
     * it registered with the hooked plugin. Hooks that leave listeners registered
     * after the plugin is disabled can cause errors when the hooked plugin calls
     * into classes that are no longer loadable.
     */
    public void disable() {

    }

}
