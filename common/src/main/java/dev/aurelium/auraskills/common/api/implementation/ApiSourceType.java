package dev.aurelium.auraskills.common.api.implementation;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.source.SourceType;
import dev.aurelium.auraskills.api.source.XpSourceParser;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;

public class ApiSourceType implements SourceType {

    private final AuraSkillsPlugin plugin;
    private final NamespacedId id;
    private final XpSourceParser<?> parser;

    public ApiSourceType(AuraSkillsPlugin plugin, NamespacedId id, XpSourceParser<?> parser) {
        this.plugin = plugin;
        this.id = id;
        this.parser = parser;
    }

    @Override
    public NamespacedId getId() {
        return id;
    }

    @Override
    public XpSourceParser<?> getParser() {
        return parser;
    }

    @Override
    public boolean isEnabled() {
        return plugin.getSkillManager().isSourceEnabled(this);
    }
}
