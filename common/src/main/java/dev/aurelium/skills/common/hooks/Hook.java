package dev.aurelium.skills.common.hooks;

import dev.aurelium.skills.common.AureliumSkillsPlugin;

public abstract class Hook {

    protected final AureliumSkillsPlugin plugin;

    public Hook(AureliumSkillsPlugin plugin) {
        this.plugin = plugin;
    }

}
