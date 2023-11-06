package dev.aurelium.auraskills.bukkit.skills.healing;

import dev.aurelium.auraskills.api.ability.Abilities;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.bukkit.ability.AbilityImpl;
import dev.aurelium.auraskills.common.message.type.AbilityMessage;
import dev.aurelium.auraskills.common.user.User;
import dev.aurelium.auraskills.api.util.NumberUtil;
import dev.aurelium.auraskills.common.util.text.TextUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

public class HealingAbilities extends AbilityImpl {

    private final String REVIVAL_HEALTH_MODIFIER_NAME = "AureliumSkills.Ability.Revival.Health";
    private final String REVIVAL_REGEN_MODIFIER_NAME = "AureliumSkills.Ability.Revival.Regeneration";

    public HealingAbilities(AuraSkills plugin) {
        super(plugin, Abilities.LIFE_ESSENCE, Abilities.HEALER, Abilities.LIFE_STEAL, Abilities.GOLDEN_HEART, Abilities.REVIVAL);
    }

    @EventHandler
    public void lifeEssence(EntityRegainHealthEvent event) {
        var ability = Abilities.LIFE_ESSENCE;

        if (isDisabled(ability)) return;

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (failsChecks(player, ability)) return;

        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC) {
            return;
        }

        User user = plugin.getUser(player);

        double multiplier = 1 + getValue(ability, user) / 100;
        event.setAmount(event.getAmount() * multiplier);
    }

    @EventHandler
    public void lifeSteal(EntityDeathEvent event) {
        var ability = Abilities.LIFE_STEAL;

        if (isDisabled(ability)) return;

        LivingEntity entity = event.getEntity();
        boolean hostile = entity instanceof Monster || entity instanceof Player || entity instanceof Phantom;

        if (!hostile) {
            return;
        }

        if (entity.getKiller() == null) return;
        Player player = entity.getKiller();
        if (player.equals(entity)) return;

        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);

        AttributeInstance entityAttribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (entityAttribute == null) return;

        // Get the health to regen from a percent of the mob's health
        double maxHealth = entityAttribute.getValue();
        double percent = getValue(ability, user) / 100;
        double healthRegen = maxHealth * percent;

        AttributeInstance playerAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (playerAttribute == null) return;

        player.setHealth(player.getHealth() + Math.min(healthRegen, playerAttribute.getValue() - player.getHealth()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void goldenHeart(EntityDamageEvent event) {
        var ability = Abilities.GOLDEN_HEART;

        if (isDisabled(ability)) return;

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);

        if (player.getAbsorptionAmount() <= 0) {
            return;
        }

        double multiplier = 1 - getValue(ability, user) / 100;
        if (multiplier < 0.01) { // Cap at 99% reduction
            multiplier = 0.01;
        }
        event.setDamage(event.getDamage() * multiplier);
    }

    @EventHandler
    public void revival(PlayerRespawnEvent event) {
        var ability = Abilities.REVIVAL;

        if (isDisabled(ability)) return;

        Player player = event.getPlayer();

        if (failsChecks(player, ability)) return;

        User user = plugin.getUser(player);

        double healthBonus = getValue(ability, user);
        double regenerationBonus = getSecondaryValue(ability, user);

        StatModifier healthModifier = new StatModifier(REVIVAL_HEALTH_MODIFIER_NAME, Stats.HEALTH, healthBonus);
        StatModifier regenerationModifier = new StatModifier(REVIVAL_REGEN_MODIFIER_NAME, Stats.REGENERATION, regenerationBonus);

        user.addStatModifier(healthModifier);
        user.addStatModifier(regenerationModifier);
        if (ability.optionBoolean("enable_message", true)) {
            Locale locale = user.getLocale();
            plugin.getAbilityManager().sendMessage(player, TextUtil.replace(plugin.getMsg(AbilityMessage.REVIVAL_MESSAGE, locale)
                    , "{value}", NumberUtil.format1(healthBonus)
                    , "{value_2}", NumberUtil.format1(regenerationBonus)));
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                user.removeStatModifier(REVIVAL_HEALTH_MODIFIER_NAME);
                user.removeStatModifier(REVIVAL_REGEN_MODIFIER_NAME);
            }
        }.runTaskLater(plugin, 30 * 20);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void revivalLeave(PlayerQuitEvent event) {
        User user = plugin.getUser(event.getPlayer());

        user.removeStatModifier(REVIVAL_HEALTH_MODIFIER_NAME);
        user.removeStatModifier(REVIVAL_REGEN_MODIFIER_NAME);
    }

}
