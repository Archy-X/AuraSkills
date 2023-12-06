package dev.aurelium.auraskills.common.stat;

import dev.aurelium.auraskills.api.stat.CustomStat;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatProvider;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.registry.Registry;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class StatRegistry extends Registry<Stat, StatProvider> {

    public StatRegistry(AuraSkillsPlugin plugin) {
        super(plugin, Stat.class, StatProvider.class);
        registerDefaults();
    }

    @Override
    public void registerDefaults() {
        for (Stat stat : Stats.values()) {
            this.register(stat.getId(), stat, plugin.getStatManager().getSupplier());
        }
    }

    public ConfigurationNode getDefinedConfig() throws SerializationException {
        ConfigurationNode root = CommentedConfigurationNode.root();
        for (Stat stat : getValues()) {
            if (!(stat instanceof CustomStat customStat)) {
                continue;
            }
            ConfigurationNode statNode = root.node("stats", stat.getId().toString());
            statNode.node("enabled").set(true);
            for (Trait trait : customStat.getDefined().getTraits().keySet()) {
                double modifier = customStat.getDefined().getTraits().getOrDefault(trait, 1.0);
                statNode.node("traits", trait.getId().toString(), "modifier").set(modifier);
            }
        }
        return root;
    }

}
