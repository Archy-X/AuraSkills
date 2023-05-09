package dev.auramc.auraskills.common.source;

import dev.auramc.auraskills.api.source.Source;
import dev.auramc.auraskills.api.source.SourceProvider;
import dev.auramc.auraskills.api.source.Sources;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.registry.Registry;

import java.util.Locale;

public class SourceRegistry extends Registry<Source, SourceProperties> implements SourceProvider {

    private final AuraSkillsPlugin plugin;

    public SourceRegistry(AuraSkillsPlugin plugin) {
        super(Source.class, SourceProperties.class);
        this.plugin = plugin;
    }

    @Override
    public void registerDefaults() {
        for (Sources source : Sources.values()) {
            SourceProperties properties = new DefaultSource(source);
            register(source.getId(), source, properties);
            // Inject SourceProvider
            injectSelf(source, SourceProvider.class);
        }
    }

    @Override
    public String getDisplayName(Source source, Locale locale) {
        return plugin.getMessageProvider().getSourceDisplayName(source, locale);
    }
}
