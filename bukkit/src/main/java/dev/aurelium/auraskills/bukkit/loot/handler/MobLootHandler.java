package dev.aurelium.auraskills.bukkit.loot.handler;

import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.api.loot.Loot;
import dev.aurelium.auraskills.api.loot.LootPool;
import dev.aurelium.auraskills.api.loot.LootTable;
import dev.aurelium.auraskills.api.loot.LootContext;
import dev.aurelium.auraskills.bukkit.loot.context.MobContext;
import dev.aurelium.auraskills.bukkit.loot.type.CommandLoot;
import dev.aurelium.auraskills.bukkit.loot.type.ItemLoot;
import dev.aurelium.auraskills.bukkit.source.EntityLeveler;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MobLootHandler extends LootHandler implements Listener {

    private final Random random = new Random();

    public MobLootHandler(AuraSkills plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        var damagerPair = getDamager(event.getEntity().getLastDamageCause());
        if (damagerPair == null) return;

        Player player = damagerPair.first();
        EntityXpSource.EntityDamagers damager = damagerPair.second();

        if (failsChecks(player, entity.getLocation())) return;

        User user = plugin.getUser(player);

        LootTable table = plugin.getLootTableManager().getLootTable(NamespacedId.fromDefault("mob"));
        if (table == null) return;

        DamageCause damageCause = getCause(event.getEntity().getLastDamageCause());

        EntityLeveler leveler = plugin.getLevelManager().getLeveler(EntityLeveler.class);
        var skillSource = leveler.getSource(entity, damager, EntityXpSource.EntityTriggers.DEATH, damageCause);
        if (skillSource == null) { // Check if the entity source is using damage trigger instead
            skillSource = leveler.getSource(entity, damager, EntityXpSource.EntityTriggers.DAMAGE, damageCause);
        }

        Skill skill = Skills.FIGHTING;
        if (skillSource != null) {
            skill = skillSource.skill();
        }

        for (LootPool pool : table.getPools()) {
            // Skip pool if no loot in the pool match the mob context
            if (isPoolUnobtainable(pool, entity.getType())) {
                continue;
            }

            double chance = getCommonChance(pool, user);

            LootDropEvent.Cause cause = LootDropEvent.Cause.MOB_LOOT_TABLE;

            MobContext context = new MobContext(entity.getType());

            double rolled = random.nextDouble();
            if (rolled < chance) {
                Loot selectedLoot = selectLoot(pool, context);
                // Give loot
                if (selectedLoot == null) {
                    break;
                }
                if (selectedLoot instanceof ItemLoot itemLoot) {
                    giveMobItemLoot(player, itemLoot, entity.getLocation(), skill, cause, table);
                } else if (selectedLoot instanceof CommandLoot commandLoot) {
                    giveCommandLoot(player, commandLoot, null, skill);
                }
                break;
            }
        }
    }

    private boolean isPoolUnobtainable(LootPool pool, EntityType entityType) {
        for (Loot loot : pool.getLoot()) {
            Set<LootContext> contexts = loot.getValues().getContexts().getOrDefault("mobs", new HashSet<>());
            // Loot will be reachable if it has no contexts
            if (contexts.isEmpty()) {
                return false;
            }
            // Loot is reachable if at least one context matches the entity type
            for (LootContext context : contexts) {
                if (context instanceof MobContext mobContext) {
                    if (mobContext.entityType().equals(entityType)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Nullable
    private Pair<Player, EntityXpSource.EntityDamagers> getDamager(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            Entity damager = entityEvent.getDamager();
            if (damager instanceof Player player) {
                return new Pair<>(player, EntityXpSource.EntityDamagers.PLAYER);
            } else if (damager instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof Player shooter) {
                    return new Pair<>(shooter, EntityXpSource.EntityDamagers.PROJECTILE);
                }
            }
        }
        return null;
    }

    private DamageCause getCause(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent entityEvent) {
            return entityEvent.getCause();
        }
        return DamageCause.CUSTOM;
    }

}
