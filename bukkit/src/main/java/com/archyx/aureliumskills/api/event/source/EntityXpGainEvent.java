package com.archyx.aureliumskills.api.event.source;

import com.archyx.aureliumskills.skills.Skill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Calls when XP is gained due to an entity, typically Fighting or Archery.
 * The event is called before any XP multipliers are applied.
 */
public class EntityXpGainEvent extends SourceXpGainEvent {

    private final LivingEntity entity;

    public EntityXpGainEvent(Player player, Skill skill, double amount, LivingEntity entity) {
        super(player, skill, amount);
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }
}
