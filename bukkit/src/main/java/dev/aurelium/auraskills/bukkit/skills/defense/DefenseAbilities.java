package dev.aurelium.auraskills.bukkit.skills.defense;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.damage.DamageMeta;
import dev.aurelium.auraskills.api.damage.DamageModifier;
import dev.aurelium.auraskills.api.event.damage.DamageEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.util.CompatUtil;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DefenseAbilities extends AbilityImpl {

    public DefenseAbilities(AuraSkills plugin) {
        super(plugin, Abilities.SHIELDING, Abilities.DEFENDER, Abilities.MOB_MASTER, Abilities.IMMUNITY, Abilities.NO_DEBUFF);
    }

    public DamageModifier shielding(User user, Player player) {
        var ability = Abilities.SHIELDING;

        if (isDisabled(ability)) return DamageModifier.none();

        if (failsChecks(player, ability)) return DamageModifier.none();

        if (!player.isSneaking()) return DamageModifier.none();

        if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

        double damageReduction = 1 - (getValue(ability, user) / 100);

        return new DamageModifier(damageReduction - 1, DamageModifier.Operation.MULTIPLY);
    }

    public DamageModifier mobMaster(DamageMeta meta, User user, Player player) {
        var ability = Abilities.MOB_MASTER;

        if (isDisabled(ability)) return DamageModifier.none();

        if (failsChecks(player, ability)) return DamageModifier.none();

        // TODO: Shouldn't this affect projectile damage from skeletons/ghasts etc?
        if (meta.getAttacker() instanceof LivingEntity && !(meta.getAttacker() instanceof Player)) {
            if (user.getAbilityLevel(ability) <= 0) return DamageModifier.none();

            double damageReduction = 1 - (getValue(ability, user) / 100);

            return new DamageModifier(damageReduction - 1, DamageModifier.Operation.MULTIPLY);
        }

        return DamageModifier.none();
    }

    public void immunity(EntityDamageByEntityEvent event, User user, Player player) {
        var ability = Abilities.IMMUNITY;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        double chance = getValue(ability, user) / 100;
        if (rand.nextDouble() < chance) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void noDebuff(PotionSplashEvent event) {
        var ability = Abilities.NO_DEBUFF;

        if (isDisabled(ability)) return;

        for (LivingEntity entity : event.getAffectedEntities()) {
            if (!(entity instanceof Player player)) continue;

            if (failsChecks(player, ability)) return;

            for (PotionEffect effect : event.getPotion().getEffects()) {
                PotionEffectType type = effect.getType();
                if (isNegativeEffect(type)) {
                    User user = plugin.getUser(player);

                    double chance = getValue(ability, user) / 100;
                    if (rand.nextDouble() < chance) {
                        if (player.hasPotionEffect(type)) continue;

                        event.setIntensity(entity, 0);
                    }
                }
            }
        }
    }

    private boolean isNegativeEffect(PotionEffectType type) {
        return type.equals(PotionEffectType.POISON) || type.equals(PotionEffectType.UNLUCK) || type.equals(PotionEffectType.WITHER) ||
                type.equals(PotionEffectType.WEAKNESS) || type.equals(PotionEffectType.HUNGER) || type.equals(PotionEffectType.BLINDNESS) ||
                CompatUtil.isEffect(type, Set.of("slowness", "slow")) ||
                CompatUtil.isEffect(type, Set.of("mining_fatigue", "slow_digging")) ||
                CompatUtil.isEffect(type, Set.of("instant_damage", "harm")) ||
                CompatUtil.isEffect(type, Set.of("nausea", "confusion"));
    }

    public void noDebuffFire(User user, Player player, LivingEntity entity) {
        var ability = Abilities.NO_DEBUFF;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (entity.getEquipment() == null) return;

        ItemStack item = entity.getEquipment().getItemInMainHand();
        if (item.getEnchantmentLevel(Enchantment.FIRE_ASPECT) > 0) {
            double chance = getValue(ability, user) / 100;
            if (rand.nextDouble() < chance) {
                plugin.getScheduler().scheduleSync(() -> player.setFireTicks(0), 50, TimeUnit.MILLISECONDS);
            }
        }
    }

    @EventHandler
    public void defenseListener(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player player)) return;
        if (player.hasMetadata("NPC")) return;
        User user = plugin.getUser(player);

        if (event.getDamager() instanceof LivingEntity entity) {
            noDebuffFire(user, player, entity);
        }

        immunity(event, user, player);
    }

    @EventHandler(ignoreCancelled = true)
    public void damageListener(DamageEvent event) {
        var meta = event.getDamageMeta();
        var target = meta.getTargetAsPlayer();

        if (target != null) {
            var user = plugin.getUser(target);
            meta.addDefenseModifier(mobMaster(meta, user, target));
            meta.addDefenseModifier(shielding(user, target));
        }
    }

}
