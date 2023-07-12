package dev.aurelium.auraskills.bukkit.leveler;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceType;

public abstract class AbstractLeveler {

    protected final AuraSkills plugin;
    private final SourceType sourceType;

    public AbstractLeveler(AuraSkills plugin, SourceType sourceType) {
        this.plugin = plugin;
        this.sourceType = sourceType;
    }

    public SourceType getSourceType() {
        return sourceType;
    }
}
