package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DefenseTrait extends TraitImpl {

    DefenseTrait(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    public void onDamage(EntityDamageByEntityEvent event, User user) {
        double reduction = user.getBonusTraitLevel(Traits.DEFENSE);
        event.setDamage(event.getDamage() * (1 - (-1.0 * Math.pow(1.01, -1.0 * reduction) + 1)));
    }
}
