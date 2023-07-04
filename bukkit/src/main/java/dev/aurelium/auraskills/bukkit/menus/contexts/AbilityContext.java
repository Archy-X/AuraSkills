package dev.aurelium.auraskills.bukkit.menus.contexts;

import com.archyx.slate.context.ContextProvider;
import dev.aurelium.auraskills.api.ability.Ability;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.jetbrains.annotations.Nullable;

public class AbilityContext implements ContextProvider<Ability> {

    private final AuraSkills plugin;

    public AbilityContext(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    @Nullable
    public Ability parse(String menuName, String input) {
        return plugin.getAbilityRegistry().getOrNull(NamespacedId.fromStringOrDefault(input));
    }
}
