package dev.aurelium.auraskills.bukkit.source;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.SkillSource;
import dev.aurelium.auraskills.api.source.type.DamageXpSource;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.api.source.type.EntityXpSource.EntityDamagers;
import dev.aurelium.auraskills.api.source.type.EntityXpSource.EntityTriggers;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.hooks.mythicmobs.MythicMobsHook;
import dev.aurelium.auraskills.bukkit.skills.fighting.FightingAbilities;
import dev.aurelium.auraskills.bukkit.util.AttributeCompat;
import dev.aurelium.auraskills.common.config.Option;
import dev.aurelium.auraskills.common.source.SourceTypes;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class EntityLeveler extends SourceLeveler {

    private final NamespacedKey SPAWNER_MOB_KEY;
    private final NamespacedKey ROSE_STACKER_SPAWNER;

    public EntityLeveler(AuraSkills plugin) {
        super(plugin, SourceTypes.ENTITY);
        this.SPAWNER_MOB_KEY = new NamespacedKey(plugin, "is_spawner_mob");
        this.ROSE_STACKER_SPAWNER = NamespacedKey.fromString("rosestacker:spawner_spawned");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (disabled()) return;
        LivingEntity entity = event.getEntity();

        if (preventMythicXp(entity)) return;

        // Ensure that the entity has a killer
        @Nullable
        Player player = entity.getKiller();
        // Ensure that the killer is an entity
        Entity damagerEntity;
        DamageCause damageCause;
        if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent) {
            damagerEntity = damageEvent.getDamager();
            damageCause = damageEvent.getCause();
        } else {
            player = getBleedDamager(entity);
            damagerEntity = player;
            damageCause = DamageCause.CUSTOM;
        }
        if (player == null) return;

        User user = plugin.getUser(player);

        // Resolve damager from EntityDamageByEntityEvent
        EntityXpSource.EntityDamagers damager;
        if (damagerEntity instanceof Player) {
            damager = EntityXpSource.EntityDamagers.PLAYER;
        } else if (damagerEntity instanceof Projectile) {
            damager = EntityXpSource.EntityDamagers.PROJECTILE;
        } else {
            return;
        }

        SkillSource<EntityXpSource> skillSource = getSource(entity, damager, EntityXpSource.EntityTriggers.DEATH, damageCause);
        if (skillSource == null) return;

        EntityXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        if (failsChecks(player, entity.getLocation(), skill)) return;

        plugin.getLevelManager().addEntityXp(user, skill, source, getSpawnerMultiplier(entity, skill) * source.getXp(),
                entity, damagerEntity, event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (disabled()) return;
        if (!(event.getEntity() instanceof LivingEntity entity) || event.getEntity() instanceof ArmorStand) {
            return;
        }

        if (preventMythicXp(entity)) return;

        // Get the player who damaged the entity and the damager type
        Pair<Player, EntityXpSource.EntityDamagers> damagerPair = resolveDamager(event.getDamager(), event.getCause());
        if (damagerPair == null) return;

        Player player = damagerPair.first();
        if (player.hasMetadata("NPC")) return;
        User user = plugin.getUser(player);
        EntityXpSource.EntityDamagers damager = damagerPair.second();

        // Get matching source with damage trigger
        SkillSource<EntityXpSource> skillSource = getSource(entity, damager, EntityXpSource.EntityTriggers.DAMAGE, event.getCause());
        if (skillSource == null) return;

        EntityXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        if (failsChecks(event, player, entity.getLocation(), skill)) return;

        double damageMultiplier = getDamageMultiplier(entity, source, event);
        plugin.getLevelManager().addEntityXp(user, skill, source, damageMultiplier * getSpawnerMultiplier(entity, skill) * source.getXp(),
                entity, event.getDamager(), event);
    }

    @EventHandler
    public void onBleedDamage(EntityDamageEvent event) {
        if (disabled()) return;
        if (!(event.getEntity() instanceof LivingEntity entity) || event.getEntity() instanceof ArmorStand) {
            return;
        }

        if (preventMythicXp(entity)) return;

        Player player = getBleedDamager(entity);
        if (player == null) return; // Was not damaged by Bleed

        if (player.hasMetadata("NPC")) return;

        User user = plugin.getUser(player);
        SkillSource<EntityXpSource> skillSource = getSource(entity, EntityDamagers.PLAYER, EntityTriggers.DAMAGE, DamageCause.CUSTOM);
        if (skillSource == null) return;

        EntityXpSource source = skillSource.source();
        Skill skill = skillSource.skill();

        if (failsChecks(player, entity.getLocation(), skill)) return;

        double damageMultiplier = getDamageMultiplier(entity, source, event);
        plugin.getLevelManager().addEntityXp(user, skill, source, damageMultiplier * getSpawnerMultiplier(entity, skill) * source.getXp(),
                entity, player, null);
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
                if (Skills.ALCHEMY.isEnabled() && plugin.configBoolean(Option.SOURCE_ENTITY_GIVE_ALCHEMY_ON_POTION_COMBAT)) {
                    return new Pair<>(player, EntityXpSource.EntityDamagers.THROWN_POTION);
                }
            }
            return new Pair<>(player, EntityXpSource.EntityDamagers.PROJECTILE);
        }
        return null;
    }

    @Nullable
    public SkillSource<EntityXpSource> getSource(LivingEntity entity, EntityXpSource.EntityDamagers eventDamager, EntityXpSource.EntityTriggers trigger, DamageCause damageCause) {
        var sources = plugin.getSkillManager().getSourcesOfType(EntityXpSource.class);
        sources = filterByTrigger(sources, trigger);

        for (SkillSource<EntityXpSource> entry : sources) {
            EntityXpSource source = entry.source();

            // Discard if entity type does not match
            String entityName = plugin.getPlatformUtil().convertEntityName(source.getEntity().toLowerCase(Locale.ROOT));
            if (!entityName.toUpperCase(Locale.ROOT).equals(entity.getType().toString())) {
                continue;
            }

            // Check if causes match
            DamageXpSource.DamageCause[] causes = source.getCauses();
            if (causes != null) {
                boolean matchingCause = false;
                for (DamageXpSource.DamageCause cause : causes) {
                    if (cause.toString().equals(damageCause.toString())) {
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
                    if (cause.toString().equals(damageCause.toString())) {
                        matchingCause = true;
                        break;
                    }
                }
                if (matchingCause) {
                    continue;
                }
            }

            // Give Alchemy XP if potion is thrown with option give_xp_on_potion_combat
            if (eventDamager == EntityXpSource.EntityDamagers.THROWN_POTION) {
                return new SkillSource<>(source, Skills.ALCHEMY);
            }

            // Return if damager matches
            for (EntityXpSource.EntityDamagers sourceDamager : source.getDamagers()) {
                if (sourceDamager == eventDamager) {
                    return entry;
                }
            }
        }
        return null;
    }

    private boolean preventMythicXp(LivingEntity entity) {
        if (plugin.getHookManager().isRegistered(MythicMobsHook.class)) {
            return plugin.getHookManager().getHook(MythicMobsHook.class).shouldPreventEntityXp(entity);
        }
        return false;
    }

    private List<SkillSource<EntityXpSource>> filterByTrigger(List<SkillSource<EntityXpSource>> sources, EntityXpSource.EntityTriggers trigger) {
        List<SkillSource<EntityXpSource>> filtered = new ArrayList<>();
        for (SkillSource<EntityXpSource> entry : sources) {
            EntityXpSource source = entry.source();
            // Check if trigger matches any of the source triggers
            for (EntityXpSource.EntityTriggers sourceTrigger : source.getTriggers()) {
                if (sourceTrigger == trigger) {
                    filtered.add(entry);
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobSplit(EntityTransformEvent event) {
        if (event.getTransformReason() != EntityTransformEvent.TransformReason.SPLIT) return;

        Entity original = event.getEntity();
        // Ignore entities that aren't spawner mobs
        if (!original.getPersistentDataContainer().has(SPAWNER_MOB_KEY, PersistentDataType.INTEGER)) return;

        // Apply key to split entities
        for (Entity entity : event.getTransformedEntities()) {
            entity.getPersistentDataContainer().set(SPAWNER_MOB_KEY, PersistentDataType.INTEGER, 1);
        }
    }

    private double getSpawnerMultiplier(Entity entity, Skill skill) {
        if (isSpawnerSpawned(entity)) {
            return skill.optionDouble("spawner_multiplier", 1.0);
        } else {
            return 1.0;
        }
    }

    private boolean isSpawnerSpawned(Entity entity) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (container.has(SPAWNER_MOB_KEY, PersistentDataType.INTEGER)) {
            return true;
        } else return container.has(ROSE_STACKER_SPAWNER, PersistentDataType.INTEGER);
    }

    @Nullable
    private Player getBleedDamager(Entity entity) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, FightingAbilities.BLEED_DAMAGER_KEY);
        if (container.has(key, PersistentDataType.STRING)) { // Handle damager from Bleed
            String uuidStr = container.get(key, PersistentDataType.STRING);
            if (uuidStr == null) return null;

            UUID uuid = UUID.fromString(uuidStr);
            return Bukkit.getPlayer(uuid);
        } else {
            return null;
        }
    }

    private double getDamageMultiplier(LivingEntity entity, EntityXpSource source, EntityDamageEvent event) {
        double damageDealt = Math.min(entity.getHealth(), event.getFinalDamage());
        if (source.scaleXpWithHealth()) {
            AttributeInstance healthAttribute = entity.getAttribute(AttributeCompat.MAX_HEALTH);
            if (healthAttribute != null) {
                double maxHealth = healthAttribute.getValue();
                return damageDealt / maxHealth; // XP gain is damage/maxHealth * sourceXp
            }
        }
        return damageDealt;
    }

}
