package dev.aurelium.auraskills.common.source.parser;

import dev.aurelium.auraskills.api.source.XpSourceParser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

public abstract class SourceParser<T> implements XpSourceParser<T> {

    protected final AuraSkillsPlugin plugin;

    public SourceParser(AuraSkillsPlugin plugin) {
        this.plugin = plugin;
    }
    
}
