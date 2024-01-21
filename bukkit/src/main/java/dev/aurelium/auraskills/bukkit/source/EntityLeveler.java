package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EntityLeveler extends SourceLeveler {

    private final NamespacedKey SPAWNER_MOB_KEY;

    public EntityLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.ENTITY);
        this.SPAWNER_MOB_KEY = new NamespacedKey(plugin, "is_spawner_mob");
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

        EntityXpSource source = sourcePair.first();
        Skill skill = sourcePair.second();

        if (failsChecks(player, entity.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(user, skill, getSpawnerMultiplier(entity, skill) * source.getXp());
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

        Player player = damagerPair.first();
        if (player.hasMetadata("NPC")) return;
        User user = plugin.getUser(player);
        EntityXpSource.EntityDamagers damager = damagerPair.second();

        // Get matching source with damage trigger
        Pair<EntityXpSource, Skill> sourcePair = getSource(entity, damager, EntityXpSource.EntityTriggers.DAMAGE);
        if (sourcePair == null) return;

        EntityXpSource source = sourcePair.first();
        Skill skill = sourcePair.second();

        if (failsChecks(event, player, entity.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(user, skill, getSpawnerMultiplier(entity, skill) * source.getXp());
    }

    @Nullable
    public Pair<Player, EntityXpSource.EntityDamagers> resolveDamager(Entity damager, EntityDamageEvent.DamageCause cause) {
        if (damager instanceof Player player) { // Player damager
            if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && cause != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                return null;
            }
            return new Pair<>(player, EntityXpSource.EntityDamagers.PLAYER);
        } else if (damager instanceof Projectile projectile) { // Projectile damager
            if (!(projectile.getShooter() instanceof Player player)) { // Make sure shooter is a player
                return null;
            }
            if (damager instanceof ThrownPotion) {
                // Mark as thrown potion if
                if (Skills.ALCHEMY.isEnabled() && Skills.ALCHEMY.optionBoolean("give_xp_on_potion_combat")) {
                    return new Pair<>(player, EntityXpSource.EntityDamagers.THROWN_POTION);
                }
            }
            return new Pair<>(player, EntityXpSource.EntityDamagers.PROJECTILE);
        }
        return null;
    }

    @Nullable
    public Pair<EntityXpSource, Skill> getSource(LivingEntity entity, EntityXpSource.EntityDamagers eventDamager, EntityXpSource.EntityTriggers trigger) {
        Map<EntityXpSource, Skill> sources = plugin.getSkillManager().getSourcesOfType(EntityXpSource.class);
        sources = filterByTrigger(sources, trigger);

        for (Map.Entry<EntityXpSource, Skill> entry : sources.entrySet()) {
            EntityXpSource source = entry.getKey();
            Skill skill = entry.getValue();

            // Discard if entity type does not match
            if (!source.getEntity().toUpperCase(Locale.ROOT).equals(entity.getType().toString())) {
                continue;
            }

            // Give Alchemy XP if potion is thrown with option give_xp_on_potion_combat
            if (eventDamager == EntityXpSource.EntityDamagers.THROWN_POTION) {
                return new Pair<>(source, Skills.ALCHEMY);
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER) {
            return;
        }
        if (!Skills.FIGHTING.isEnabled() && !Skills.ARCHERY.isEnabled()) {
            return;
        }
        // Don't mark if multiplier is default
        if (Skills.FIGHTING.optionDouble("spawner_multiplier", 1) == 1.0 && Skills.ARCHERY.optionDouble("spawner_multiplier", 1) == 1.0) {
            return;
        }
        LivingEntity entity = event.getEntity();
        PersistentDataContainer data = entity.getPersistentDataContainer();
        data.set(SPAWNER_MOB_KEY, PersistentDataType.INTEGER, 1);
    }

    private double getSpawnerMultiplier(Entity entity, Skill skill) {
        if (entity.getPersistentDataContainer().has(SPAWNER_MOB_KEY, PersistentDataType.INTEGER)) { // Is spawner mob
            return skill.optionDouble("spawner_multiplier", 1.0);
        } else {
            return 1.0;
        }
    }

}
