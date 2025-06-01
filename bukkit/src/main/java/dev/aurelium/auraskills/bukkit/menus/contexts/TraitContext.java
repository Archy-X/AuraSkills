package dev.aurelium.auraskills.bukkit.menus.contexts;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;

public class TraitContext implements ContextProvider<Trait> {

    private final AuraSkills plugin;

    public TraitContext(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<Trait> getType() {
        return Trait.class;
    }

    @Override
    @Nullable
    public Trait parse(String menuName, String input) {
        Trait trait = plugin.getTraitRegistry().getOrNull(NamespacedId.fromDefault(input));
        if (trait != null && trait.isEnabled()) {
            return trait;
        } else {
            return null;
        }
    }

}
