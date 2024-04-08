package dev.aurelium.auraskills.bukkit.menus.contexts;

import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.slate.context.ContextProvider;
import org.jetbrains.annotations.Nullable;

public class StatContext implements ContextProvider<Stat> {

    private final AuraSkills plugin;

    public StatContext(AuraSkills plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<Stat> getType() {
        return Stat.class;
    }

    @Nullable
    @Override
    public Stat parse(String menuName, String input) {
        Stat stat = plugin.getStatRegistry().getOrNull(NamespacedId.fromDefault(input));
        if (stat != null && stat.isEnabled()) {
            return stat;
        } else {
            return null;
        }
    }

}
