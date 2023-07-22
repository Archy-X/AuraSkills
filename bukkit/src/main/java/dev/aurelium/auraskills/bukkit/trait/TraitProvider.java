package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class TraitProvider implements Listener {

    protected final AuraSkills plugin;
    private final Trait trait;

    TraitProvider(AuraSkills plugin, Trait trait) {
        this.plugin = plugin;
        this.trait = trait;
    }

    public double getTraitLevel(Player player) {
        User user = plugin.getUser(player);
        return user.getTraitLevel(trait);
    }

}
