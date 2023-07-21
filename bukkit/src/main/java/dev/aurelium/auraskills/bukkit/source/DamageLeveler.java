package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.type.DamageXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.source.SourceType;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Map;

public class DamageLeveler extends SourceLeveler {

    public DamageLeveler(AuraSkills plugin) {
        super(plugin, SourceType.DAMAGE);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("deprecation")
    public void onDamage(EntityDamageEvent event) {
        if (disabled()) return;
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        var sourcePair = getSource(event);
        if (sourcePair == null) return;

        DamageXpSource source = sourcePair.getFirst();
        Skill skill = sourcePair.getSecond();

        // Disregard self inflected damage
        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            if (isSelfInflicted(entityEvent.getDamager(), player)) return;
        }

        if (failsChecks(event, player, player.getLocation(), skill)) return;

        // Check shield blocking option
        if (skill.equals(Skills.DEFENSE) && !plugin.configBoolean(Option.DEFENSE_ALLOW_SHIELD_BLOCKING) && player.isBlocking()) {
            return;
        }

        double damage;
        if (source.useOriginalDamage()) {
            damage = event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
        } else {
            damage = event.getFinalDamage();
        }

        // Check if player survives
        if (source.mustSurvive() && event.getFinalDamage() >= player.getHealth()) {
            return;
        }

        double xp = damage * source.getXp();

        // Adjust to max and min for defense
        if (skill.equals(Skills.DEFENSE)) {
            xp = Math.min(xp, plugin.configDouble(Option.DEFENSE_MAX));
            xp = Math.max(xp, plugin.configDouble(Option.DEFENSE_MIN));
        }

        User user = plugin.getUser(player);

        plugin.getLevelManager().addXp(user, skill, xp);
    }

    private boolean isSelfInflicted(Entity damager, Player player) {
        if (damager instanceof Player) {
            return damager.equals(player);
        } else if (damager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                return shooter.equals(player);
            }
        }
        return false;
    }

    private Pair<DamageXpSource, Skill> getSource(EntityDamageEvent event) {
        var sources = plugin.getSkillManager().getSourcesOfType(DamageXpSource.class);
        for (Map.Entry<DamageXpSource, Skill> entry : sources.entrySet()) {
            DamageXpSource source = entry.getKey();
            // Check if causes match
            DamageXpSource.DamageCause[] causes = source.getCauses();
            if (causes != null) {
                boolean matchingCause = false;
                for (DamageXpSource.DamageCause cause : causes) {
                    if (cause.toString().equals(event.getCause().toString())) {
                        matchingCause = true;
                        break;
                    }
                }
                if (!matchingCause) {
                    continue;
                }
            }
            // Check excluded causes
            DamageXpSource.DamageCause[] excludedCauses = source.getExcludedCauses();
            if (excludedCauses != null) {
                boolean matchingCause = false;
                for (DamageXpSource.DamageCause cause : excludedCauses) {
                    if (cause.toString().equals(event.getCause().toString())) {
                        matchingCause = true;
                        break;
                    }
                }
                if (matchingCause) {
                    continue;
                }
            }
            // Check damager
            if (source.getDamager() != null && event instanceof EntityDamageByEntityEvent entityEvent) {
                if (!damagerMatches(entityEvent.getDamager(), source.getDamager())) {
                    continue;
                }
            }
            return new Pair<>(source, entry.getValue());
        }
        return null;
    }

    private boolean damagerMatches(Entity damager, String name) {
        if (name.equalsIgnoreCase("mob")) {
            return damager instanceof Mob;
        } else {
            return damager.getType().toString().equalsIgnoreCase(name);
        }
    }

}
