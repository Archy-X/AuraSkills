package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public class MovementSpeedTrait extends TraitImpl {

    MovementSpeedTrait(AuraSkills plugin) {
        super(plugin, Traits.MOVEMENT_SPEED);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 100;
    }

    @Override
    protected void reload(Player player, Trait trait) {

    }
}
