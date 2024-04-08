package dev.aurelium.auraskills.bukkit.menus.contexts;

import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;

public class ManaAbilityContext implements ContextProvider<ManaAbility> {

    private final AuraSkills plugin;

    public ManaAbilityContext(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<ManaAbility> getType() {
        return ManaAbility.class;
    }

    @Override
    @Nullable
    public ManaAbility parse(String menuName, String input) {
        ManaAbility manaAbility = plugin.getManaAbilityRegistry().getOrNull(NamespacedId.fromDefault(input));
        if (manaAbility != null && manaAbility.isEnabled()) {
            return manaAbility;
        } else {
            return null;
        }
    }
}
