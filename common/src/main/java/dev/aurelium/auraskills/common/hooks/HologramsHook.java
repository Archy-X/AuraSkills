package dev.aurelium.auraskills.common.hooks;

import dev.aurelium.auraskills.api.util.LocationHolder;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class HologramsHook extends Hook {

    public HologramsHook(AuraSkillsPlugin plugin, ConfigurationNode config) {
        super(plugin, config);
    }

    public abstract void createHologram(LocationHolder locationHolder, String text);

}
