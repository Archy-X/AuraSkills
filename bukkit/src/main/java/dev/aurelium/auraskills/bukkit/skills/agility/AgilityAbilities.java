package dev.aurelium.auraskills.bukkit.skills.agility;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.event.trait.CustomRegenEvent;
import dev.aurelium.auraskills.api.event.user.UserLoadEvent;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.util.PotionUtil;
import dev.aurelium.auraskills.common.message.type.AbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Locale;

public class AgilityAbilities extends AbilityImpl {

    public static final String FLEETING_ID = "auraskills/fleeting";

    public AgilityAbilities(AuraSkills plugin) {
        super(plugin, Abilities.LIGHT_FALL, Abilities.JUMPER, Abilities.SUGAR_RUSH, Abilities.FLEETING, Abilities.THUNDER_FALL);
    }

    private void lightFall(EntityDamageEvent event, User user, Player player) {
        var ability = Abilities.LIGHT_FALL;

        if (isDisabled(ability)) return;
        if (failsChecks(player, ability)) return;
        
        if (!(event.getFinalDamage() > 0.0)) return;
        
        double percentReduction = getValue(ability, user);
        event.setDamage(event.getDamage() * (1 - (percentReduction / 100)));
    }

    // For potion splashes
    @EventHandler(priority = EventPriority.HIGH)
    public void sugarRushSplash(PotionSplashEvent event) {
        var ability = Abilities.SUGAR_RUSH;

        if (event.isCancelled()) return;

        if (isDisabled(ability)) return;

        for (PotionEffect effect : event.getPotion().getEffects()) {
            if (!effect.getType().equals(PotionEffectType.SPEED) && !effect.getType().equals(PotionEffectType.JUMP)) continue;

            for (LivingEntity entity : event.getAffectedEntities()) {
                if (!(entity instanceof Player player)) continue;

                if (failsChecks(player, ability)) return;

                User user = plugin.getUser(player);

                if (user.getAbilityLevel(ability) <= 0) continue;

                double intensity = event.getIntensity(player);
                double multiplier = 1 + (getValue(ability, user) / 100);
                PotionUtil.applyEffect(player, new PotionEffect(effect.getType(), (int) (effect.getDuration() * multiplier * intensity), effect.getAmplifier()));
            }
        }
    }

    public double getSugarRushSplashMultiplier(Player player) {
        var ability = Abilities.SUGAR_RUSH;

        if (isDisabled(ability)) return 1.0;

        if (failsChecks(player, ability)) return 1.0;

        if (player.hasPermission("auraskills.skill.agility")) {
            User user = plugin.getUser(player);
            if (user.getAbilityLevel(ability) > 0) {
                return 1 + (getValue(ability, user) / 100);
            }
        }
        return 1.0;
    }

    // For potion drinking
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH)
    public void sugarRushDrink(PlayerItemConsumeEvent event) {
        var ability = Abilities.SUGAR_RUSH;

        if (event.isCancelled()) return;

        if (isDisabled(ability)) return;

        Player player = event.getPlayer();
        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);
        ItemStack item = event.getItem();

        if (item.getType() != Material.POTION) return;
        if (!(item.getItemMeta() instanceof PotionMeta meta)) return;

        PotionData potion = meta.getBasePotionData();
        double multiplier = 1 + (getValue(ability, user) / 100);

        if (potion.getType() == PotionType.SPEED || potion.getType() == PotionType.JUMP) {
            int amplifier = 0;
            if (potion.isUpgraded()) {
                amplifier = 1;
            }
            PotionEffectType potionEffectType;
            if (potion.getType() == PotionType.SPEED) {
                potionEffectType = PotionEffectType.SPEED;
            } else {
                potionEffectType = PotionEffectType.JUMP;
            }
            int duration;
            if (potion.isExtended()) {
                duration = 480;
            } else if (potion.isUpgraded()) {
                duration = 90;
            } else {
                duration = 180;
            }
            duration = (int) (multiplier * duration);
            PotionUtil.applyEffect(player, new PotionEffect(potionEffectType, duration * 20, amplifier));
        }
        // Apply custom effects
        if (meta.hasCustomEffects()) {
            for (PotionEffect effect : meta.getCustomEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED) || effect.getType().equals(PotionEffectType.JUMP)) {
                    PotionUtil.applyEffect(player, new PotionEffect(effect.getType(), (int) (effect.getDuration() * multiplier), effect.getAmplifier()));
                }
            }
        }
    }

    public void fleeting(EntityDamageEvent event, User user, Player player) {
        var ability = Abilities.FLEETING;

        if (isDisabled(ability)) return;
        if (failsChecks(player, ability)) return;

        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) return;

        double maxHealth = attribute.getValue();

        if (player.getHealth() - event.getFinalDamage() < getFleetingHealthRequired() * maxHealth) {
            if (user.getTraitModifier(FLEETING_ID) != null) {
                return;
            }
            double percent = getValue(ability, user);
            user.addTraitModifier(new TraitModifier(FLEETING_ID, Traits.MOVEMENT_SPEED, percent));

            Locale locale = user.getLocale();
            plugin.getAbilityManager().sendMessage(player, TextUtil.replace(plugin.getMsg(AbilityMessage.FLEETING_START, locale), "{value}", String.valueOf((int) percent)));
        }
    }

    public void removeFleeting(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        if (attribute == null) return;

        double maxHealth = attribute.getValue();
        if (player.getHealth() >= getFleetingHealthRequired() * maxHealth) {
            removeFleetingMetadata(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void fleetingEnd(EntityRegainHealthEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player)) return;

        fleetingRemove((Player) event.getEntity(), event.getAmount());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void fleetingEndCustom(CustomRegenEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        fleetingRemove(player, event.getAmount());
    }

    private void fleetingRemove(Player player, double amountRegenerated) {
        var ability = Abilities.FLEETING;

        if (failsChecks(player, ability)) return;

        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) return;

        double maxHealth = attribute.getValue();
        if (player.getHealth() + amountRegenerated >= getFleetingHealthRequired() * maxHealth) {
            removeFleetingMetadata(player);
        }
    }

    private void removeFleetingMetadata(Player player) {
        User user = plugin.getUser(player);

        // Returns if there was no modifier to remove
        if (!user.removeTraitModifier(FLEETING_ID)) {
            return;
        }

        Locale locale = plugin.getUser(player).getLocale();
        plugin.getAbilityManager().sendMessage(player, plugin.getMsg(AbilityMessage.FLEETING_END, locale));
    }

    @EventHandler
    public void fleetingLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getUser(player).removeTraitModifier(FLEETING_ID);
    }

    @EventHandler
    public void fleetingJoin(UserLoadEvent event) {
        var ability = Abilities.FLEETING;

        if (isDisabled(ability)) return;

        Player player = event.getPlayer();

        if (failsChecks(player, ability)) return;

        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) return;

        if (player.getHealth() < getFleetingHealthRequired() * attribute.getValue()) {
            User user = plugin.getUser(player);

            double percent = getValue(ability, user);

            user.addTraitModifier(new TraitModifier(FLEETING_ID, Traits.MOVEMENT_SPEED, percent));
        }
    }

    @EventHandler
    public void fleetingDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        plugin.getUser(player).removeTraitModifier(FLEETING_ID);
    }

    private double getFleetingHealthRequired() {
        double healthPercentRequired = Abilities.FLEETING.optionDouble("health_percent_required", 20);
        return healthPercentRequired / 100;
    }

    public void thunderFall(EntityDamageEvent event, User user, Player player) {
        var ability = Abilities.THUNDER_FALL;

        if (isDisabled(ability)) return;
        if (failsChecks(player, ability)) return;

        if (!player.isSneaking()) return;
        // If chance
        if (rand.nextDouble() < getValue(ability, user) / 100) {
            // Get damage values
            double percent = getSecondaryValue(ability, user);
            double thunderFallDamage = (percent / 100) * event.getDamage();
            // Get entities nearby
            for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 3, 3, 1)) {
                if (!(entity instanceof LivingEntity livingEntity) || entity.equals(player)) continue;

                // Damage the entity
                livingEntity.damage(thunderFallDamage, player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void agilityListener(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player player)) return;
        if (player.hasMetadata("NPC")) return;

        User user = plugin.getUser(player);
        // If from fall damage
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            thunderFall(event, user, player);

            lightFall(event, user, player);
        }
        fleeting(event, user, player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void mealSteal(EntityDamageByEntityEvent event) {
        var ability = Abilities.MEAL_STEAL;

        if (isDisabled(ability)) return;

        if (event.isCancelled()) return;
        // Checks if entity and damager are players
        if (!(event.getEntity() instanceof Player enemy) || !(event.getDamager() instanceof Player player)) return;

        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);
        // Calculates chance
        double chance = getValue(ability, user) / 100;
        if (rand.nextDouble() < chance) {
            // Removes food from enemy
            if (enemy.getFoodLevel() >= 1) {
                enemy.setFoodLevel(enemy.getFoodLevel() - 1);
            }
            // Adds food level to player
            if (player.getFoodLevel() < 20) {
                player.setFoodLevel(player.getFoodLevel() + 1);
            }
            // Adds saturation if food is full
            else if (player.getSaturation() < 20f) {
                player.setSaturation(player.getSaturation() + 1f);
            }
        }
    }

}
