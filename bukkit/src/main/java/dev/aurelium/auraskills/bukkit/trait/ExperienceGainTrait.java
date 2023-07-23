package dev.aurelium.auraskills.bukkit.trait;

import dev.aurelium.auraskills.api.trait.Trait;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class ExperienceGainTrait extends TraitImpl {

    ExperienceGainTrait(AuraSkills plugin) {
        super(plugin);
    }

    @Override
    public double getBaseLevel(Player player, Trait trait) {
        return 0;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        //Check for disabled world
        if (plugin.getWorldManager().isInDisabledWorld(player.getLocation())) {
            return;
        }
        User user = plugin.getUser(player);
        event.setAmount((int) (event.getAmount() * (1 + user.getEffectiveTraitLevel(Traits.EXPERIENCE_GAIN))));
    }

}
