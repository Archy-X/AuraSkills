package dev.aurelium.auraskills.bukkit.leveler;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.source.SourceType;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EntityLeveler extends AbstractLeveler {

    public EntityLeveler(AuraSkills plugin) {
        super(plugin, SourceType.ENTITY);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
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

        Pair<EntityXpSource, Skill> sourcePair = getSource(entity, damageEvent, EntityXpSource.EntityTriggers.DEATH);
        if (sourcePair == null) {
            return;
        }

        EntityXpSource source = sourcePair.getFirst();
        Skill skill = sourcePair.getSecond();

        if (failsChecks(player, entity.getLocation(), skill)) return;

        plugin.getLevelManager().addXp(user, skill, source.getXp());
    }

    @Nullable
    private Pair<EntityXpSource, Skill> getSource(LivingEntity entity, EntityDamageByEntityEvent event, EntityXpSource.EntityTriggers trigger) {
        Map<EntityXpSource, Skill> sources = plugin.getSkillManager().getSourcesOfType(EntityXpSource.class);
        sources = filterByTrigger(sources, trigger);

        for (Map.Entry<EntityXpSource, Skill> entry : sources.entrySet()) {
            EntityXpSource source = entry.getKey();
            Skill skill = entry.getValue();

            // Discard if entity type does not match
            if (!source.getEntity().equals(entity.getType().toString())) {
                continue;
            }

            // Return if damager matches
            for (EntityXpSource.EntityDamagers damager : source.getDamagers()) {
                if (damager == EntityXpSource.EntityDamagers.PLAYER) {
                    if (event.getDamager() instanceof Player) {
                        return new Pair<>(source, skill);
                    }
                } else if (damager == EntityXpSource.EntityDamagers.PROJECTILE) {
                    if (event.getDamager() instanceof Projectile) {
                        return new Pair<>(source, skill);
                    }
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
