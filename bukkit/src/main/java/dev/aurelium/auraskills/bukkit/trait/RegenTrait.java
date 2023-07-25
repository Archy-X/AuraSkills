package dev.aurelium.auraskills.bukkit.trait;

import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class RegenTrait extends TraitImpl {

    public RegenTrait(AuraSkills plugin) {
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
        User user = plugin.getUser(player);
        if (player.getSaturation() > 0) {
            if (!Traits.SATURATION_REGEN.isEnabled()) return;
            event.setAmount(event.getAmount() + user.getBonusTraitLevel(Traits.SATURATION_REGEN));
        } else if (player.getFoodLevel() >= 14) {
            if (!Traits.HUNGER_REGEN.isEnabled()) return;
            event.setAmount(event.getAmount() + user.getBonusTraitLevel(Traits.HUNGER_REGEN));
        }
    }

}
