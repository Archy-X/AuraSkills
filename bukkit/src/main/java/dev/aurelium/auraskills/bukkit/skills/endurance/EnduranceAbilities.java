package dev.aurelium.auraskills.bukkit.skills.endurance;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.event.trait.CustomRegenEvent;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.bukkit.user.BukkitUser;
import dev.aurelium.auraskills.common.user.User;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class EnduranceAbilities extends AbilityImpl {

    public EnduranceAbilities(AuraSkills plugin) {
        super(plugin, Abilities.ANTI_HUNGER, Abilities.RUNNER, Abilities.GOLDEN_HEAL, Abilities.RECOVERY, Abilities.MEAL_STEAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void antiHunger(FoodLevelChangeEvent event) {
        var ability = Abilities.ANTI_HUNGER;

        if (isDisabled(ability)) return;

        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (failsChecks(player, ability)) return;
        // Checks if food level would be decreased
        if (player.getFoodLevel() <= event.getFoodLevel()) return;

        User user = plugin.getUser(player);
        double chance = getValue(ability, user) / 100;
        if (rand.nextDouble() < chance) {
            event.setFoodLevel(player.getFoodLevel());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void goldenHealAndRecovery(EntityRegainHealthEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player player)) return;

        User user = plugin.getUser(player);
        // Golden Heal
        if (event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.MAGIC_REGEN)) {
            var ability = Abilities.GOLDEN_HEAL;

            if (isDisabled(ability)) return;
            if (failsChecks(player, ability)) return;
            // Applies modifier
            double modifier = getValue(ability, user) / 100;
            event.setAmount(event.getAmount() * (1 + modifier));
        }
        // Recovery
        else if (event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            var ability = Abilities.RECOVERY;

            if (isDisabled(ability)) return;
            if (failsChecks(player, ability)) return;
            // Gets health
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute == null) return;

            double currentHealth = player.getHealth();
            double maxHealth = attribute.getValue();
            // Checks if health is less than half of max
            if (currentHealth < (maxHealth / 2)) {
                // Applies modifier
                double modifier = getValue(ability, user) / 100;
                event.setAmount(event.getAmount() * (1 + modifier));
            }
        }
    }

    @EventHandler
    public void recoveryCustom(CustomRegenEvent event) {
        var ability = Abilities.RECOVERY;
        if (event.isCancelled()) return;

        if (isDisabled(ability)) return;
        Player player = BukkitUser.getPlayer(event.getUser());

        User user = plugin.getUser(player);
        // Gets health
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute == null) return;

        double currentHealth = player.getHealth();
        double maxHealth = attribute.getValue();
        // Checks if health is less than half of max
        if (currentHealth < (maxHealth / 2)) {
            // Applies modifier
            double modifier = getValue(ability, user) / 100;
            event.setAmount(event.getAmount() * (1 + modifier));
        }
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
