package dev.auramc.auraskills.common.hooks;

import dev.auramc.auraskills.common.AuraSkillsPlugin;

public abstract class Hook {

    protected final AuraSkillsPlugin plugin;

    public Hook(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

}
