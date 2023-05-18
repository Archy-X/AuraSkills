package dev.auramc.auraskills.common.source.trigger;

import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.data.PlayerData;
import dev.auramc.auraskills.common.source.SkillSource;

public abstract class SourceTrigger {

    private final AuraSkillsPlugin plugin;

    SourceTrigger(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }

    protected void trigger(PlayerData playerData, SkillSource source) {
        plugin.getLeveler().addXp(playerData, source.skill(), source.value());
    }

}
