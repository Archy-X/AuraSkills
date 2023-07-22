package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class TraitImpl implements Listener {

    protected final AuraSkills plugin;

    TraitImpl(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public abstract double getBaseLevel(Player player, Trait trait);

}
