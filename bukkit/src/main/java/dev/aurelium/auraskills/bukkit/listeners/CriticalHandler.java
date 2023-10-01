package dev.aurelium.auraskills.bukkit.listeners;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.trait.CritChanceTrait;
import dev.aurelium.auraskills.bukkit.trait.CritDamageTrait;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CriticalHandler {

    private final Random rand = new Random();
    private final AuraSkills plugin;

    public CriticalHandler(AuraSkills plugin) {
        this.plugin = plugin;
    }

    public void applyCrit(EntityDamageByEntityEvent event, Player player, User user) {
        if (!Abilities.CRIT_CHANCE.isEnabled()) {
            return;
        }

        if (isCrit(user)) {
            event.setDamage(event.getDamage() * getCritMultiplier(user));
            player.setMetadata("skillsCritical", new FixedMetadataValue(plugin, true));
            plugin.getScheduler().scheduleSync(() -> player.removeMetadata("skillsCritical", plugin), 50, TimeUnit.MILLISECONDS);
        }
    }

    private boolean isCrit(User user) {
        return plugin.getTraitManager().getTraitImpl(CritChanceTrait.class).isCrit(user);
    }

    private double getCritMultiplier(User user) {
        return plugin.getTraitManager().getTraitImpl(CritDamageTrait.class).getCritMultiplier(user);
    }

}
