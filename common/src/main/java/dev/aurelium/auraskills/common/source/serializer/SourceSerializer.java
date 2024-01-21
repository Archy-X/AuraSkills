package dev.aurelium.auraskills.common.source.serializer;

import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.api.source.XpSourceSerializer;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

public abstract class SourceSerializer<T> extends XpSourceSerializer<T> {

    protected final AuraSkillsPlugin plugin;

    public SourceSerializer(AuraSkillsPlugin plugin, SourceType sourceType, String sourceName) {
        super(plugin.getApi(), sourceType, sourceName);
        this.plugin = plugin;
    }
    
}
