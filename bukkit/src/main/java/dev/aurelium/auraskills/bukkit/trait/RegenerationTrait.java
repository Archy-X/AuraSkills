package dev.aurelium.auraskills.bukkit.trait;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class RegenerationTrait extends TraitImpl {

    public RegenerationTrait(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            return;
        }
        // Check for disabled world
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return;
        }
        if (OptionL.getBoolean(Option.REGENERATION_CUSTOM_REGEN_MECHANICS)) {
            event.setCancelled(true);
            return;
        }
        if (player.getSaturation() > 0) {
            event.setAmount(event.getAmount() + getTraitLevel(player, Traits.SATURATION_REGENERATION));
        } else if (player.getFoodLevel() >= 14) {
            event.setAmount(event.getAmount() + getTraitLevel(player, Traits.HUNGER_REGENERATION));
        }
    }

}
