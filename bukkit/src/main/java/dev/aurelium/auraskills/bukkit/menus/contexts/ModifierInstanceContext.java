package dev.aurelium.auraskills.bukkit.menus.contexts;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.menus.shared.ModifierInstance;
import dev.aurelium.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;

public class ModifierInstanceContext implements ContextProvider<ModifierInstance> {

    private final AuraSkills plugin;

    public ModifierInstanceContext(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<ModifierInstance> getType() {
        return ModifierInstance.class;
    }

    @Override
    @Nullable
    public ModifierInstance parse(String menuName, String input) {
        return null;
    }
}
