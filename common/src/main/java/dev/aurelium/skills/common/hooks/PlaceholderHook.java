package dev.aurelium.skills.common.hooks;

import dev.aurelium.skills.common.AureliumSkillsPlugin;
import dev.aurelium.skills.common.data.PlayerData;

public abstract class PlaceholderHook extends Hook {

    public PlaceholderHook(AureliumSkillsPlugin plugin) {
        super(plugin);
    }

    public abstract String setPlaceholders(PlayerData playerData, String message);

}
