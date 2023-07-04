package dev.aurelium.auraskills.bukkit.menus.contexts;

import com.archyx.slate.context.ContextProvider;
import dev.aurelium.auraskills.api.mana.ManaAbility;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.jetbrains.annotations.Nullable;

public class ManaAbilityContext implements ContextProvider<ManaAbility> {

    private final AuraSkills plugin;

    public ManaAbilityContext(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    @Nullable
    public ManaAbility parse(String menuName, String input) {
        return plugin.getManaAbilityRegistry().getOrNull(NamespacedId.fromStringOrDefault(input));
    }
}
