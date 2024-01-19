package dev.aurelium.auraskills.bukkit.loot.handler;

import dev.aurelium.auraskills.api.event.loot.LootDropEvent;
import dev.aurelium.auraskills.api.registry.NamespacedId;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.source.type.EntityXpSource;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.loot.Loot;
import dev.aurelium.auraskills.bukkit.loot.LootPool;
import dev.aurelium.auraskills.bukkit.loot.LootTable;
import dev.aurelium.auraskills.bukkit.loot.context.MobContext;
import dev.aurelium.auraskills.bukkit.loot.type.CommandLoot;
import dev.aurelium.auraskills.bukkit.loot.type.ItemLoot;
import dev.aurelium.auraskills.bukkit.source.EntityLeveler;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.data.Pair;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

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

        EntityLeveler leveler = plugin.getLevelManager().getLeveler(EntityLeveler.class);
        var sourcePair = leveler.getSource(entity, damager, EntityXpSource.EntityTriggers.DEATH);
        if (sourcePair == null) { // Check if the entity source is using damage trigger instead
            sourcePair = leveler.getSource(entity, damager, EntityXpSource.EntityTriggers.DAMAGE);
        }

        Skill skill = Skills.FIGHTING;
        if (sourcePair != null) {
            skill = sourcePair.second();
        }

        for (LootPool pool : table.getPools()) {
            double chance = getCommonChance(pool, user);

            LootDropEvent.Cause cause = LootDropEvent.Cause.MOB_LOOT_TABLE;

            MobContext context = new MobContext(entity.getType());

            if (random.nextDouble() < chance) {
                Loot selectedLoot = selectLoot(pool, context);
                // Give loot
                if (selectedLoot == null) {
                    break;
                }
                if (selectedLoot instanceof ItemLoot itemLoot) {
                    giveMobItemLoot(player, itemLoot, entity.getLocation(), skill, cause);
                } else if (selectedLoot instanceof CommandLoot commandLoot) {
                    giveCommandLoot(player, commandLoot, null, skill);
                }
                break;
            }
        }
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

}
