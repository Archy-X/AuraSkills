package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.config.MainConfig;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;

public class ApiMainConfig implements MainConfig {

    private final AuraSkillsPlugin plugin;

    public ApiMainConfig(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isDisabledInCreative() {
        return plugin.configBoolean(Option.DISABLE_IN_CREATIVE_MODE);
    }

    @Override
    public int getStartLevel() {
        return plugin.config().getStartLevel();
    }

    @Override
    public int getHighestMaxLevel() {
        return plugin.config().getHighestMaxLevel();
    }
}
