package dev.auramc.auraskills.common.hooks;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;

public abstract class PlaceholderHook extends Hook {

    public PlaceholderHook(AuraSkillsPlugin plugin) {
        super(plugin);
    }

    public abstract String setPlaceholders(PlayerData playerData, String message);

}
