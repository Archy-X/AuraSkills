package dev.aurelium.auraskills.common.hooks;

import dev.aurelium.auraskills.common.AuraSkillsPlugin;

public abstract class Hook {

    protected final AuraSkillsPlugin plugin;

    public Hook(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

}
