package dev.aurelium.auraskills.common.trait;

import dev.aurelium.auraskills.api.trait.CustomTrait;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.TraitProvider;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.registry.Registry;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class TraitRegistry extends Registry<Trait, TraitProvider> {

    public TraitRegistry(AuraSkillsPlugin plugin) {
        super(plugin, Trait.class, TraitProvider.class);
        registerDefaults();
    }

    @Override
    public void registerDefaults() {
        for (Trait trait : Traits.values()) {
            this.register(trait.getId(), trait, plugin.getTraitManager().getSupplier());
        }
    }

    public ConfigurationNode getDefinedConfig() throws SerializationException {
        ConfigurationNode root = CommentedConfigurationNode.root();
        for (Trait trait : getValues()) {
            if (!(trait instanceof CustomTrait)) {
                continue;
            }
            ConfigurationNode traitNode = root.node("traits", trait.getId().toString());
            traitNode.node("enabled").set(true);
        }
        return root;
    }

}
