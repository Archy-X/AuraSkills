package dev.aurelium.auraskills.bukkit.menus.contexts;

import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;

public class AbilityContext implements ContextProvider<Ability> {

    private final AuraSkills plugin;

    public AbilityContext(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<Ability> getType() {
        return Ability.class;
    }

    @Override
    @Nullable
    public Ability parse(String menuName, String input) {
        Ability ability = plugin.getAbilityRegistry().getOrNull(NamespacedId.fromDefault(input));
        if (ability != null && ability.isEnabled()) {
            return ability;
        } else {
            return null;
        }
    }
}
