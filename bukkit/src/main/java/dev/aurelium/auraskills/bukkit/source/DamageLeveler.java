package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.type.DamageXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;

public class DamageLeveler extends SourceLeveler {

    public DamageLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.DAMAGE);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("deprecation")
    public void onDamage(EntityDamageEvent event) {
        if (disabled()) return;
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (player.hasMetadata("NPC")) return;

        var sourcePair = getSource(event);
        if (sourcePair == null) return;

        DamageXpSource source = sourcePair.first();
        Skill skill = sourcePair.second();

        // Disregard self inflected damage
        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            if (isSelfInflicted(entityEvent.getDamager(), player)) return;
        }

        if (failsChecks(event, player, player.getLocation(), skill)) return;

        // Check shield blocking option
        if (skill.equals(Skills.DEFENSE) && !Skills.DEFENSE.optionBoolean("allow_shield_blocking") && player.isBlocking()) {
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
            xp = Math.min(xp, Skills.DEFENSE.optionDouble("max"));
            // Returns if xp is less than the min
            if (xp < Skills.DEFENSE.optionDouble("min")) {
                return;
            }
        }

        User user = plugin.getUser(player);

        plugin.getLevelManager().addXp(user, skill, source, xp);
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
            if (source.getDamager() != null) {
                if (event instanceof EntityDamageByEntityEvent entityEvent) {
                    if (!damagerMatches(source, entityEvent.getDamager(), source.getDamager())) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            return Pair.fromEntry(entry);
        }
        return null;
    }

    private boolean damagerMatches(DamageXpSource source, Entity damager, String name) {
        if (source.includeProjectiles() && damager instanceof Projectile projectile) {
            return projectileSourceMatches(projectile.getShooter(), name);
        } else {
            return entityDamagerMatches(damager, name);
        }
    }

    private boolean entityDamagerMatches(Entity damager, String name) {
        if (name.equalsIgnoreCase("mob")) {
            return damager instanceof Mob;
        } else {
            return damager.getType().toString().equalsIgnoreCase(name);
        }
    }

    private boolean projectileSourceMatches(ProjectileSource projectileSource, String name) {
        if (name.equalsIgnoreCase("mob")) {
            return projectileSource instanceof Mob;
        } else if (projectileSource instanceof LivingEntity livingEntity) {
            return livingEntity.getType().toString().equalsIgnoreCase(name);
        }
        return false;
    }

}
