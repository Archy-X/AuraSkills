package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.DamageXpSource;
import dev.aurelium.auraskills.api.source.type.DamageXpSource.DamageCause;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class DamageLeveler extends SourceLeveler {

    private final Map<UUID, Long> lastGainTime = new HashMap<>();

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

        var skillSource = getSource(event);
        if (skillSource == null) return;

        DamageXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        // Check cooldown
        long lastGain = lastGainTime.getOrDefault(player.getUniqueId(), 0L);
        int cooldownMs = source.getCooldownMs();
        if (lastGain + cooldownMs > System.currentTimeMillis()) {
            return;
        }

        // Disregard self inflected damage
        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            if (isSelfInflicted(entityEvent.getDamager(), player)) {
                return;
            }

            // Check shield blocking option
            if (skill.equals(Skills.DEFENSE)
                    && !Skills.DEFENSE.optionBoolean("allow_shield_blocking", false)
                    && isShieldingSuccessful(entityEvent.getDamager(), player)) {
                return;
            }
        }

        if (failsChecks(event, player, player.getLocation(), skill)) return;

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
            xp = Math.min(xp, Skills.DEFENSE.optionDouble("max", 100.0));
            // Returns if xp is less than the min
            if (xp < Skills.DEFENSE.optionDouble("min", 0)) {
                return;
            }
        }

        User user = plugin.getUser(player);

        Entity damager = null;
        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            damager = entityEvent.getDamager();
        }

        plugin.getLevelManager().addDamageXp(user, skill, source, xp,
                DamageCause.valueOf(event.getCause().name()), damager, event);

        // Mark last gained time
        lastGainTime.put(player.getUniqueId(), System.currentTimeMillis());
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

    private SkillSource<DamageXpSource> getSource(EntityDamageEvent event) {
        var sources = plugin.getSkillManager().getSourcesOfType(DamageXpSource.class);
        for (SkillSource<DamageXpSource> entry : sources) {
            DamageXpSource source = entry.source();
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
            // Check excluded damagers
            if (source.getExcludedDamagers() != null) {
                if (event instanceof EntityDamageByEntityEvent entityEvent) {
                    if (Arrays.stream(source.getExcludedDamagers())
                            .anyMatch(excludedDamager -> damagerMatches(source, entityEvent.getDamager(), excludedDamager))) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            // Check damager
            if (source.getDamagers() != null) {
                if (event instanceof EntityDamageByEntityEvent entityEvent) {
                    if (Arrays.stream(source.getDamagers())
                            .noneMatch(damagerName -> damagerMatches(source, entityEvent.getDamager(), damagerName))) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            return entry;
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

    private boolean isShieldingSuccessful(Entity source, Player subject) {

        if (source instanceof AbstractArrow entityarrow) {
            if (entityarrow.getPierceLevel() > 0) {
                return false;
            }
        }

        if (!subject.isBlocking()) {
            return false;
        }

        Vector vec3d = source.getLocation().toVector();
        Location location = subject.getLocation();

        Vector vec3d1 = this.calculateViewVector(0.0F, location.getYaw());
        Vector vec3d2 = vec3d.subtract( location.toVector() );

        vec3d2 = new Vector(vec3d2.getX(), 0.0D, vec3d2.getY()).normalize();
        return vec3d2.dot(vec3d1) < 0.0D;
    }

    public final Vector calculateViewVector(float pitch, float yaw) {
        float f2 = pitch * 0.017453292F;
        float f3 = -yaw * 0.017453292F;
        float f4 = cos(f3);
        float f5 = sin(f3);
        float f6 = cos(f2);
        float f7 = sin(f2);

        return new Vector(f5 * f6, -f7, (double) (f4 * f6));
    }

    //region match minecraft implementation, might need to be moved to a more appropriate package
    private static final float[] SIN = make(new float[65536], sineTable -> {
        for (int ix = 0; ix < sineTable.length; ix++) {
            sineTable[ix] = (float)Math.sin((double)ix * Math.PI * 2.0 / 65536.0);
        }
    });

    private static <T> T make(T object, Consumer<? super T> initializer) {
        initializer.accept(object);
        return object;
    }

    private static float sin(float value) {
        return SIN[(int)(value * 10430.378F) & 65535];
    }

    private static float cos(float value) {
        return SIN[(int)(value * 10430.378F + 16384.0F) & 65535];
    }
    //endregion

}
