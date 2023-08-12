package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;

public class MaxManaTrait extends TraitImpl {

    MaxManaTrait(AuraSkills plugin) {
        super(plugin, Traits.MAX_MANA);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 20;
    }

}
