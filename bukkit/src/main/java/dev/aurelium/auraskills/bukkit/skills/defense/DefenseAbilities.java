package dev.aurelium.auraskills.bukkit.skills.defense;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
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

import java.util.concurrent.TimeUnit;

public class DefenseAbilities extends AbilityImpl {

    public DefenseAbilities(AuraSkills plugin) {
        super(plugin, Abilities.SHIELDING, Abilities.DEFENDER, Abilities.MOB_MASTER, Abilities.IMMUNITY, Abilities.NO_DEBUFF);
    }

    public void shielding(EntityDamageByEntityEvent event, User user, Player player) {
        var ability = Abilities.SHIELDING;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (!player.isSneaking()) return;

        if (user.getAbilityLevel(ability) <= 0) return;

        double damageReduction = 1 - (getValue(ability, user) / 100);
        event.setDamage(event.getDamage() * damageReduction);
    }

    public void mobMaster(EntityDamageByEntityEvent event, User user, Player player) {
        var ability = Abilities.MOB_MASTER;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (event.getDamager() instanceof LivingEntity && !(event.getDamager() instanceof Player)) {
            if (user.getAbilityLevel(ability) <= 0) return;

            double damageReduction = 1 - (getValue(ability, user) / 100);
            event.setDamage(event.getDamage() * damageReduction);
        }
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
                if (type.equals(PotionEffectType.POISON) || type.equals(PotionEffectType.UNLUCK) || type.equals(PotionEffectType.WITHER) ||
                        type.equals(PotionEffectType.WEAKNESS) || type.equals(PotionEffectType.SLOW_DIGGING) || type.equals(PotionEffectType.SLOW) ||
                        type.equals(PotionEffectType.HUNGER) || type.equals(PotionEffectType.HARM) || type.equals(PotionEffectType.CONFUSION) ||
                        type.equals(PotionEffectType.BLINDNESS)) {
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

    public void noDebuffFire(User user, Player player, LivingEntity entity) {
        var ability = Abilities.NO_DEBUFF;

        if (isDisabled(ability)) return;

        if (failsChecks(player, ability)) return;

        if (entity.getEquipment() == null) return;

        ItemStack item = entity.getEquipment().getItemInMainHand();
        if (item.getEnchantmentLevel(Enchantment.FIRE_ASPECT) > 0) {
            double chance = getValue(ability, user) / 100;
            if (rand.nextDouble() < chance) {
                plugin.getScheduler().scheduleSync(() -> {
                    player.setFireTicks(0);
                }, 50, TimeUnit.MILLISECONDS);
            }
        }
    }

    @EventHandler
    public void defenseListener(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player player)) return;

        User user = plugin.getUser(player);

        if (event.getDamager() instanceof LivingEntity entity) {
            noDebuffFire(user, player, entity);
        }

        immunity(event, user, player);
    }

}
