package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.config.MainConfig;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.hooks.EconomyHook;

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

    @Override
    public boolean isJobsEnabled() {
        return plugin.configBoolean(Option.JOBS_ENABLED) && plugin.getHookManager().isRegistered(EconomyHook.class);
    }

    @Override
    public boolean jobSelectionEnabled() {
        return plugin.config().jobSelectionEnabled();
    }
}
