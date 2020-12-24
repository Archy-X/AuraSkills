package com.archyx.aureliumskills.abilities;

import com.archyx.aureliumskills.AureliumSkills;
import com.archyx.aureliumskills.configuration.Option;
import com.archyx.aureliumskills.configuration.OptionL;
import com.archyx.aureliumskills.skills.PlayerSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Critical {

    private final Random r = new Random();
    private final Plugin plugin;

    public Critical(Plugin plugin) {
        this.plugin = plugin;
    }

    public void applyCrit(EntityDamageByEntityEvent event, Player player, PlayerSkill playerSkill) {
        if (AureliumSkills.abilityManager.isEnabled(Ability.CRIT_CHANCE)) {
            if (isCrit(playerSkill)) {
                event.setDamage(event.getDamage() * getCritMultiplier(playerSkill));
                player.setMetadata("skillsCritical", new FixedMetadataValue(plugin, true));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.removeMetadata("skillsCritical", plugin);
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
    }

    private boolean isCrit(PlayerSkill playerSkill) {
        return r.nextDouble() < (Ability.CRIT_CHANCE.getValue(playerSkill.getAbilityLevel(Ability.CRIT_CHANCE)) / 100);
    }

    private double getCritMultiplier(PlayerSkill playerSkill) {
        if (AureliumSkills.abilityManager.isEnabled(Ability.CRIT_DAMAGE)) {
            double multiplier = Ability.CRIT_DAMAGE.getValue(playerSkill.getAbilityLevel(Ability.CRIT_DAMAGE)) / 100;
            return OptionL.getDouble(Option.CRITICAL_BASE_MULTIPLIER) * (1 + multiplier);
        }
        return OptionL.getDouble(Option.CRITICAL_BASE_MULTIPLIER);
    }

}
