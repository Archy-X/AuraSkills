package dev.aurelium.auraskills.bukkit.leveler;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceType;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EntityLeveler extends AbstractLeveler {

    public EntityLeveler(AuraSkills plugin) {
        super(plugin, SourceType.ENTITY);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (disabled()) return;
        LivingEntity entity = event.getEntity();
        // Ensure that the entity has a killer
        if (entity.getKiller() == null) {
            return;
        }
        // Ensure that the killer is an entity
        if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent)) {
            return;
        }

        Player player = entity.getKiller();
        User user = plugin.getUser(player);

        // Resolve damager from EntityDamageByEntityEvent
        EntityXpSource.EntityDamagers damager;
        if (damageEvent.getDamager() instanceof Player) {
            damager = EntityXpSource.EntityDamagers.PLAYER;
        } else if (damageEvent.getDamager() instanceof Projectile) {
            damager = EntityXpSource.EntityDamagers.PROJECTILE;
        } else {
            return;
        }

        Pair<EntityXpSource, Skill> sourcePair = getSource(entity, damager, EntityXpSource.EntityTriggers.DEATH);
        if (sourcePair == null) return;

        EntityXpSource source = sourcePair.getFirst();
        Skill skill = sourcePair.getSecond();

        if (failsChecks(player, entity.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(user, skill, source.getXp());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (disabled()) return;
        if (!(event.getEntity() instanceof LivingEntity entity) || event.getEntity() instanceof ArmorStand) {
            return;
        }
        // Get the player who damaged the entity and the damager type
        Pair<Player, EntityXpSource.EntityDamagers> damagerPair = resolveDamager(event.getDamager(), event.getCause());
        if (damagerPair == null) return;

        Player player = damagerPair.getFirst();
        User user = plugin.getUser(player);
        EntityXpSource.EntityDamagers damager = damagerPair.getSecond();

        // Get matching source with damage trigger
        Pair<EntityXpSource, Skill> sourcePair = getSource(entity, damager, EntityXpSource.EntityTriggers.DAMAGE);
        if (sourcePair == null) return;

        EntityXpSource source = sourcePair.getFirst();
        Skill skill = sourcePair.getSecond();

        if (failsChecks(event, player, entity.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(user, skill, source.getXp());
    }

    @Nullable
    private Pair<Player, EntityXpSource.EntityDamagers> resolveDamager(Entity damager, EntityDamageEvent.DamageCause cause) {
        if (damager instanceof Player player) { // Player damager
            if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                return null;
            }
            return new Pair<>(player, EntityXpSource.EntityDamagers.PLAYER);
        } else if (damager instanceof Projectile projectile) { // Projectile damager
            // TODO Implement ALCHEMY_GIVE_XP_ON_POTION_COMBAT
            if (!(projectile.getShooter() instanceof Player player)) { // Make sure shooter is a player
                return null;
            }
            return new Pair<>(player, EntityXpSource.EntityDamagers.PROJECTILE);
        }
        return null;
    }

    @Nullable
    private Pair<EntityXpSource, Skill> getSource(LivingEntity entity, EntityXpSource.EntityDamagers eventDamager, EntityXpSource.EntityTriggers trigger) {
        Map<EntityXpSource, Skill> sources = plugin.getSkillManager().getSourcesOfType(EntityXpSource.class);
        sources = filterByTrigger(sources, trigger);

        for (Map.Entry<EntityXpSource, Skill> entry : sources.entrySet()) {
            EntityXpSource source = entry.getKey();
            Skill skill = entry.getValue();

            // Discard if entity type does not match
            if (!source.getEntity().toUpperCase(Locale.ROOT).equals(entity.getType().toString())) {
                continue;
            }

            // Return if damager matches
            for (EntityXpSource.EntityDamagers sourceDamager : source.getDamagers()) {
                if (sourceDamager == eventDamager) {
                    return new Pair<>(source, skill);
                }
            }
        }
        return null;
    }

    private Map<EntityXpSource, Skill> filterByTrigger(Map<EntityXpSource, Skill> sources, EntityXpSource.EntityTriggers trigger) {
        Map<EntityXpSource, Skill> filtered = new HashMap<>();
        for (Map.Entry<EntityXpSource, Skill> entry : sources.entrySet()) {
            EntityXpSource source = entry.getKey();
            Skill skill = entry.getValue();
            // Check if trigger matches any of the source triggers
            for (EntityXpSource.EntityTriggers sourceTrigger : source.getTriggers()) {
                if (sourceTrigger == trigger) {
                    filtered.put(source, skill);
                    break;
                }
            }
        }
        return filtered;
    }

}
