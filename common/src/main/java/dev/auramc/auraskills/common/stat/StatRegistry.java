package dev.auramc.auraskills.common.stat;

import dev.auramc.auraskills.api.stat.Stat;
import dev.auramc.auraskills.api.stat.StatProvider;
import dev.auramc.auraskills.api.stat.Stats;
import dev.auramc.auraskills.common.AuraSkillsPlugin;
import dev.auramc.auraskills.common.registry.Registry;

import java.util.Locale;

public class StatRegistry extends Registry<Stat, StatProperties> implements StatProvider {

    private final AuraSkillsPlugin plugin;

    public StatRegistry(AuraSkillsPlugin plugin) {
        super(Stat.class, StatProperties.class);
        this.plugin = plugin;
    }

    @Override
    public void registerDefaults() {
        for (Stats stat : Stats.values()) {
            StatProperties properties = new DefaultStat(stat);
            register(stat.getId(), stat, properties);
            // Inject StatProvider
            injectSelf(stat, StatProvider.class);
        }
    }

    @Override
    public String getDisplayName(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatDisplayName(stat, locale);
    }

    @Override
    public String getDescription(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatDescription(stat, locale);
    }

    @Override
    public String getColor(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatColor(stat, locale);
    }

    @Override
    public String getSymbol(Stat stat, Locale locale) {
        return plugin.getMessageProvider().getStatSymbol(stat, locale);
    }
}
